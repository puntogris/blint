package com.puntogris.blint.data.repo

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.puntogris.blint.data.local.AppDatabase
import com.puntogris.blint.data.remote.FirestoreQueries
import com.puntogris.blint.data.repo.imp.IBackupRepository
import com.puntogris.blint.model.Business
import com.puntogris.blint.utils.BackupState
import com.puntogris.blint.utils.Constants
import com.puntogris.blint.utils.Constants.BACKUP_PATH
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.Util.copyFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

class BackupRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val appDatabase: AppDatabase,
    private val firestoreQueries: FirestoreQueries
    ): IBackupRepository {

    override fun checkBackUpRequirements(): StateFlow<RepoResult<List<Business>>> =
        MutableStateFlow<RepoResult<List<Business>>>(RepoResult.InProgress).also { result ->
            firestoreQueries.getUserLocalBusinessQuery().get()
                .addOnSuccessListener {
                    val business = it.toObjects(Business::class.java)
                    if (business.isNotEmpty()) result.value = RepoResult.Success(business)
                    else result.value = RepoResult.Error(Exception())
                }
                .addOnFailureListener {
                    result.value = RepoResult.Error(it)
                }
    }

    override fun checkLastBackUpDate(): StateFlow<RepoResult<Long>> =
        MutableStateFlow<RepoResult<Long>>(RepoResult.InProgress).also { result ->
            firestoreQueries.getUserBackupStorageQuery()
                .metadata
                .addOnSuccessListener { result.value = RepoResult.Success(it.creationTimeMillis) }
                .addOnFailureListener { result.value = RepoResult.Error(it) }
        }

    override fun createBackupForBusiness(path: String) =
        MutableStateFlow<BackupState>(BackupState.InProgress()).also {
            appDatabase.close()
            val dbRef = firestoreQueries.getUserBackupStorageQuery()
            val zipFile = File(path)
            val stream = FileInputStream(zipFile)

            dbRef.putStream(stream)
                .addOnProgressListener{
                    val progress = (it.bytesTransferred / it.totalByteCount) * 100
                    BackupState.InProgress(progress)
                }
                .addOnSuccessListener{ BackupState.Success }
                .addOnFailureListener{ BackupState.Error(it) }
        }

    override fun restoreBackupForBusiness(path: String) =
        MutableStateFlow<BackupState>(BackupState.InProgress()).also {
            try {
                appDatabase.close()
                val dbRef = firestoreQueries.getUserBackupStorageQuery()

                val localFile = File(context.filesDir.path + "/${BACKUP_PATH}")

                if (!localFile.exists()) localFile.parentFile?.mkdirs()
                dbRef.getFile(localFile)
                    .addOnProgressListener{
                        val progress = (it.bytesTransferred / it.totalByteCount) * 100
                        BackupState.InProgress(progress)
                    }
                    .addOnSuccessListener{
                       copyFile(
                           File(localFile.path).inputStream(),
                           File(path).outputStream())
                        BackupState.Success
                    }
                    .addOnFailureListener{ BackupState.Error(it) }
            }catch (e:Exception){
                BackupState.Error(e)
            }
        }
}