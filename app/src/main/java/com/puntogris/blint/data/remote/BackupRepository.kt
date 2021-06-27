package com.puntogris.blint.data.remote

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.puntogris.blint.data.local.AppDatabase
import com.puntogris.blint.data.local.dao.ProductsDao
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Employee
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
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import javax.inject.Inject

class BackupRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val appDatabase: AppDatabase
    ):IBackupRepository {

    private val storage =  Firebase.storage.reference
    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    fun getCurrentUid() = auth.currentUser?.uid

    override fun checkBackUpRequirements(): StateFlow<RepoResult<List<Business>>> =
        MutableStateFlow<RepoResult<List<Business>>>(RepoResult.InProgress).also { result ->
            firestore
                .collectionGroup("business")
                .whereEqualTo("owner", getCurrentUid())
                .whereEqualTo("type", "LOCAL").get()
                .addOnSuccessListener { snap ->
                    if (!snap.documents.isNullOrEmpty()) {
                        val data = snap.toObjects(Business::class.java)
                        result.value = RepoResult.Success(data)
                    }
                }
                .addOnFailureListener { result.value = RepoResult.Error(it) }
        }

    override fun checkLastBackUpDate(): StateFlow<RepoResult<Long>> =
        MutableStateFlow<RepoResult<Long>>(RepoResult.InProgress).also { result ->
            val dbRef = storage.child("users/${auth.currentUser?.uid}/backup")
            dbRef.metadata
                .addOnSuccessListener {
                result.value = RepoResult.Success(it.creationTimeMillis)
                }
                .addOnFailureListener {
                    result.value = RepoResult.Error(it)
                }
        }

    //hacer esta funcion sin suspend y con state flow para poder mostrar el progreso? idk
    //ponerla linda que esta un desastre
    override suspend fun createBackupForBusiness(path: String):SimpleResult {
        return try {
            appDatabase.close()

            val dbRef = storage.child("users/${auth.currentUser?.uid}/backup")

            val zipFile = File(path)
            withContext(Dispatchers.IO) {
                val stream = FileInputStream(zipFile)
                dbRef.putStream(stream).await()
            }
            SimpleResult.Success
        }
        catch (e: Exception){
            SimpleResult.Failure
        }
    }

    override suspend fun restoreBackupForBusiness(path: String): SimpleResult  {
        return try {
            appDatabase.close()
            val dbRef = storage.child("users/${auth.currentUser?.uid}/backup")

            val localFile = File(context.filesDir.path + "/backup")

            if (!localFile.exists()) localFile.parentFile?.mkdirs()
            dbRef.getFile(localFile).await()

            copyFile(
                File(localFile.path).inputStream(),
                File(path).outputStream())

            SimpleResult.Success
        }catch (e: Exception){
            SimpleResult.Failure
        }
    }


}