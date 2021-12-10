package com.puntogris.blint.feature_store.data.repository

import android.content.Context
import com.google.firebase.storage.StorageException
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.DispatcherProvider
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

    override suspend fun getUser() = appDatabase.usersDao.getUser()

    override suspend fun syncUserAccount(authUser: AuthUser): SyncAccount =
        withContext(dispatcher.io) {
            try {
                val user = userServerApi.getUserAccount(authUser)
                appDatabase.usersDao.insert(user)

                val business = appDatabase.businessDao.getBusiness()

                if (business.isNotEmpty()) {
                    appDatabase.businessDao.updateBusinessesOwnerUid(user.uid)
                    appDatabase.usersDao.updateCurrentBusiness(business.first().businessId)
                    sharedPreferences.setShowNewUserScreen(false)
                    SyncAccount.Success.HasBusiness
                } else {
                    sharedPreferences.setShowNewUserScreen(true)
                    SyncAccount.Success.BusinessNotFound
                }.also {
                    sharedPreferences.setShowLoginScreen(false)
                }

            } catch (e: Exception) {
                SyncAccount.Error(e)
            }
        }

    override suspend fun updateCurrentBusiness(businessId: String): SimpleResource =
        withContext(dispatcher.io) {
            SimpleResource.build {
                appDatabase.usersDao.updateCurrentBusiness(businessId)
            }
        }


    override fun sendTicket(ticket: Ticket): Flow<SimpleProgressResource> = flow {
        try {
            emit(ProgressResource.InProgress)

            if (!ticket.isValid()) {
                emit(ProgressResource.Error(R.string.snack_ticket_missing_required_data))
                return@flow
            }

            userServerApi.sendTicket(ticket)

            emit(ProgressResource.Success(Unit))
        } catch (e: Exception) {
            emit(ProgressResource.Error(R.string.snack_error_connection_server_try_later))
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
    override fun createBackup(): Flow<BackupState> = flow {
        try {
            emit(BackupState.Loading)
            appDatabase.close()

            val path = context.getDatabasePath(Constants.LOCAL_DATABASE_NAME).absolutePath
            val zipFile = File(path)
            val stream = FileInputStream(zipFile)
            userServerApi.uploadBackup(stream)

            emit(BackupState.BackupSuccess)
        } catch (e: Exception) {
            emit(BackupState.Error(R.string.snack_error_connection_server_try_later))
        }
    }.flowOn(dispatcher.io)

    override fun restoreBackup(): Flow<BackupState> = flow {
        try {
            emit(BackupState.Loading)
            appDatabase.close()

            val path = context.getDatabasePath(Constants.LOCAL_DATABASE_NAME).absolutePath

            val localFile = File(context.filesDir.path + "/backup")
            if (!localFile.exists()) localFile.parentFile?.mkdirs()

            userServerApi.downloadBackup(localFile)
            Util.copyFile(File(localFile.path).inputStream(), File(path).outputStream())

            emit(BackupState.BackupSuccess)
        } catch (e: Exception) {
            emit(BackupState.Error(R.string.snack_error_connection_server_try_later))
        }
    }.flowOn(dispatcher.io)

    override suspend fun deleteUserAccount(email: String): Flow<SimpleProgressResource> = flow {
        try {
            emit(ProgressResource.InProgress)

            if (email != appDatabase.usersDao.getUser().email) {
                emit(ProgressResource.Error(R.string.snack_account_email_not_matching))
                return@flow
            }

            userServerApi.deleteAccount()
            appDatabase.clearAllTables()

            emit(ProgressResource.Success(Unit))
        } catch (e: Exception) {
            emit(ProgressResource.Error(R.string.snack_error_connection_server_try_later))
        }
    }
}