package com.puntogris.blint.feature_store.data.repository

import android.content.Context
import com.google.firebase.storage.StorageException
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.Util
import com.puntogris.blint.common.utils.types.BackupState
import com.puntogris.blint.common.utils.types.RepoResult
import com.puntogris.blint.common.utils.types.SimpleRepoResult
import com.puntogris.blint.common.utils.types.SyncAccount
import com.puntogris.blint.feature_store.data.data_source.local.AppDatabase
import com.puntogris.blint.feature_store.data.data_source.local.SharedPreferences
import com.puntogris.blint.feature_store.data.data_source.remote.UserServerApi
import com.puntogris.blint.feature_store.domain.model.AuthUser
import com.puntogris.blint.feature_store.domain.model.Ticket
import com.puntogris.blint.feature_store.domain.model.User
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

    override suspend fun syncUserAccount(authUser: AuthUser?): SyncAccount =
        withContext(dispatcher.io) {
            try {
                val user = if (authUser != null) userServerApi.getUserAccount(authUser) else User()
                appDatabase.usersDao.insert(user)

                val business = appDatabase.businessDao.getBusiness()

                if (business.isNotEmpty()) {
                    appDatabase.businessDao.updateBusinessesOwnerUid(user.uid)
                    appDatabase.usersDao.updateCurrentBusiness(business.first().businessId)
                    sharedPreferences.setShowNewUserScreenPref(true)
                    SyncAccount.Success.HasBusiness
                } else {
                    sharedPreferences.setShowNewUserScreenPref(false)
                    SyncAccount.Success.BusinessNotFound
                }.also {
                    sharedPreferences.setShowLoginScreen(false)
                }

            } catch (e: Exception) {
                SyncAccount.Error(e)
            }
        }


    override fun sendTicket(ticket: Ticket): Flow<SimpleRepoResult> = flow {
        try {
            emit(RepoResult.InProgress)

            if (!ticket.isValid()) emit(RepoResult.Error(R.string.snack_ticket_missing_required_data))
//
//            firebase.firestore
//                .collection(Constants.TICKETS_COLLECTION)
//                .document(ticket.ticketId)
//                .set(ticket)
//                .await()

            emit(RepoResult.Success(Unit))
        } catch (e: Exception) {
            emit(RepoResult.Error(R.string.snack_error_connection_server_try_later))
        }
    }.flowOn(dispatcher.io)

    private fun getUserBackupStorageQuery() = ""
    //firebase.storage.child("${Constants.USERS_PATH}/${firebase.currentUid}/${Constants.BACKUP_PATH}_${BuildConfig.VERSION_NAME}")

    override fun checkLastBackUpDate(): Flow<BackupState> = flow {
        try {
            emit(BackupState.Loading)
            // val metadata = getUserBackupStorageQuery().metadata.await().creationTimeMillis
            // emit(BackupState.ShowLastBackup(metadata))
        } catch (e: StorageException) {
            when (e.errorCode) {
                StorageException.ERROR_NOT_AUTHENTICATED -> {
                    emit(BackupState.Error(R.string.fast_login))
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

    override fun createBackup(path: String): Flow<BackupState> = flow {
        try {
            emit(BackupState.Loading)
            appDatabase.close()

            val dbRef = getUserBackupStorageQuery()
            val zipFile = File(path)
            val stream = FileInputStream(zipFile)

            // dbRef.putStream(stream).await()

            emit(BackupState.BackupSuccess)
        } catch (e: Exception) {
            emit(BackupState.Error(R.string.snack_error_connection_server_try_later))
        }
    }.flowOn(dispatcher.io)

    override fun restoreBackup(path: String): Flow<BackupState> = flow {
        try {
            emit(BackupState.Loading)
            appDatabase.close()

            val dbRef = getUserBackupStorageQuery()
            val localFile = File(context.filesDir.path + "/${Constants.BACKUP_PATH}")

            if (!localFile.exists()) localFile.parentFile?.mkdirs()
           //  dbRef.getFile(localFile).await()

            Util.copyFile(File(localFile.path).inputStream(), File(path).outputStream())

            emit(BackupState.BackupSuccess)
        } catch (e: Exception) {
            emit(BackupState.Error(R.string.snack_error_connection_server_try_later))
        }
    }.flowOn(dispatcher.io)
}