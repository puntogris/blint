package com.puntogris.blint.data.repository.backup

import android.content.Context
import com.google.firebase.storage.StorageException
import com.puntogris.blint.BuildConfig
import com.puntogris.blint.R
import com.puntogris.blint.data.data_source.local.AppDatabase
import com.puntogris.blint.data.data_source.remote.FirebaseClients
import com.puntogris.blint.utils.Constants.BACKUP_PATH
import com.puntogris.blint.utils.Constants.USERS_PATH
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.Util.copyFile
import com.puntogris.blint.utils.types.BackupState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

class BackupRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val appDatabase: AppDatabase,
    private val firebase: FirebaseClients,
    private val dispatcher: DispatcherProvider
) : IBackupRepository {

    private fun getUserBackupStorageQuery() =
        firebase.storage.child("$USERS_PATH/${firebase.currentUid}/${BACKUP_PATH}_${BuildConfig.VERSION_NAME}")

    override fun checkLastBackUpDate(): Flow<BackupState> = flow {
        try {
            emit(BackupState.Loading)
            val metadata = getUserBackupStorageQuery().metadata.await().creationTimeMillis
            emit(BackupState.ShowLastBackup(metadata))
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

            dbRef.putStream(stream).await()

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
            val localFile = File(context.filesDir.path + "/${BACKUP_PATH}")

            if (!localFile.exists()) localFile.parentFile?.mkdirs()
            dbRef.getFile(localFile).await()

            copyFile(File(localFile.path).inputStream(), File(path).outputStream())

            emit(BackupState.BackupSuccess)
        } catch (e: Exception) {
            emit(BackupState.Error(R.string.snack_error_connection_server_try_later))
        }
    }.flowOn(dispatcher.io)
}