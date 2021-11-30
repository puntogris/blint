package com.puntogris.blint.feature_store.data.repository

import android.content.Context
import com.google.firebase.storage.StorageException
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.Util
import com.puntogris.blint.common.utils.types.*
import com.puntogris.blint.feature_store.data.data_source.local.AppDatabase
import com.puntogris.blint.feature_store.data.data_source.local.SharedPreferences
import com.puntogris.blint.feature_store.data.data_source.remote.UserServerApi
import com.puntogris.blint.feature_store.domain.model.AuthUser
import com.puntogris.blint.feature_store.domain.model.Ticket
import com.puntogris.blint.feature_store.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream

class UserRepositoryImpl(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val sharedPreferences: SharedPreferences,
    private val dispatcher: DispatcherProvider,
    private val userServerApi: UserServerApi
) : UserRepository {

    override fun getUserFlow() = appDatabase.usersDao.getUserFlow()

    override suspend fun syncUserAccount(authUser: AuthUser): SyncAccount =
        withContext(dispatcher.io) {
            try {
                val user = userServerApi.getUserAccount(authUser)
                appDatabase.usersDao.insert(user)

                val business = appDatabase.businessDao.getBusiness()

                if (business.isNotEmpty()) {
                    appDatabase.businessDao.updateBusinessesOwnerUid(user.uid)
                    appDatabase.usersDao.updateCurrentBusiness(business.first().businessId)
                    sharedPreferences.setShowNewUserScreen(true)
                    SyncAccount.Success.HasBusiness
                } else {
                    sharedPreferences.setShowNewUserScreen(false)
                    SyncAccount.Success.BusinessNotFound
                }.also {
                    sharedPreferences.setShowLoginScreen(false)
                }

            } catch (e: Exception) {
                SyncAccount.Error(e)
            }
        }

    override suspend fun updateCurrentBusiness(businessId: String): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                appDatabase.usersDao.updateCurrentBusiness(businessId)
            }
        }


    override fun sendTicket(ticket: Ticket): Flow<SimpleRepoResult> = flow {
        try {
            emit(RepoResult.InProgress)

            if (!ticket.isValid()) emit(RepoResult.Error(R.string.snack_ticket_missing_required_data))

            userServerApi.sendTicket(ticket)

            emit(RepoResult.Success(Unit))
        } catch (e: Exception) {
            emit(RepoResult.Error(R.string.snack_error_connection_server_try_later))
        }
    }.flowOn(dispatcher.io)


    override fun checkLastBackupTimestamp(): Flow<BackupState> = flow {
        try {
            emit(BackupState.Loading)
            val timestamp = userServerApi.getLastBackupTimestamp()
            emit(BackupState.ShowLastBackup(timestamp))
        } catch (e: StorageException) {
            when (e.errorCode) {
                StorageException.ERROR_NOT_AUTHENTICATED -> {
                    emit(BackupState.Error(R.string.snack_an_error_occurred))
                }
                StorageException.ERROR_OBJECT_NOT_FOUND -> {
                    emit(BackupState.ShowLastBackup())
                }
                else -> {
                    emit(BackupState.Error())
                }
            }
        } catch (e: Exception) {
            emit(BackupState.Error(R.string.snack_error_connection_server_try_later))
        }
    }.flowOn(dispatcher.io)


    @Suppress("BlockingMethodInNonBlockingContext")
    override fun createBackup(path: String): Flow<BackupState> = flow {
        try {
            emit(BackupState.Loading)
            appDatabase.close()

            val zipFile = File(path)
            val stream = FileInputStream(zipFile)
            userServerApi.uploadBackup(stream)

            emit(BackupState.BackupSuccess)
        } catch (e: Exception) {
            emit(BackupState.Error(R.string.snack_error_connection_server_try_later))
        }
    }.flowOn(dispatcher.io)

    override fun restoreBackup(path: String): Flow<BackupState> = flow {
        try {
            emit(BackupState.Loading)
            appDatabase.close()

            val localFile = File(context.filesDir.path + "/backup")
            if (!localFile.exists()) localFile.parentFile?.mkdirs()

            userServerApi.downloadBackup(localFile)
            Util.copyFile(File(localFile.path).inputStream(), File(path).outputStream())

            emit(BackupState.BackupSuccess)
        } catch (e: Exception) {
            emit(BackupState.Error(R.string.snack_error_connection_server_try_later))
        }
    }.flowOn(dispatcher.io)
}