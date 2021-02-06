package com.puntogris.blint.data.remote

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.puntogris.blint.data.local.AppDatabase
import com.puntogris.blint.data.local.dao.ProductsDao
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.Util.copyFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import javax.inject.Inject

class BackupRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val appDatabase: AppDatabase,
    private val productsDao: ProductsDao
):IBackupRepository {

    private val storage =  Firebase.storage.reference
    private val auth = FirebaseAuth.getInstance()

    //hacer esta funcion sin suspend y con state flow para poder mostrar el progreso? idk
    //ponerla linda que esta un desastre
    override suspend fun createBackupForBusiness(businessID: String, path: String):SimpleResult {
        return try {
            appDatabase.close()

            val dbRef = storage.child("users/${auth.currentUser?.uid}/backup/$businessID")

            val zipFile = File(path)
            withContext(Dispatchers.IO) {
                val stream = FileInputStream(zipFile)
                dbRef.putStream(stream).await()
            }
            SimpleResult.Success
        }
        catch (e: Exception){
            println(e.localizedMessage)
            SimpleResult.Failure
        }
    }


    override suspend fun restoreBackupForBusiness(businessID: String, path: String): SimpleResult  {
        return try {
            appDatabase.close()
            val dbRef = storage.child("users/${auth.currentUser?.uid}/backup/$businessID")

            val localFile = File(context.filesDir.path + "/backup/$businessID", businessID)

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