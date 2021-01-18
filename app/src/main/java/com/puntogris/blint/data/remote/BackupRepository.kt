package com.puntogris.blint.data.remote

import android.content.Context
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.puntogris.blint.data.local.businesses.BusinessDatabase
import com.puntogris.blint.data.local.clients.ClientsDatabase
import com.puntogris.blint.data.local.products.ProductsDao
import com.puntogris.blint.data.local.products.ProductsDatabase
import com.puntogris.blint.data.local.records.RecordsDatabase
import com.puntogris.blint.data.local.suppliers.SuppliersDatabase
import com.puntogris.blint.ui.main.MainActivity
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.Util.zipDatabases
import com.wwdablu.soumya.wzip.WZip
import com.wwdablu.soumya.wzip.WZipCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.security.auth.callback.Callback

class BackupRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val productsDatabase: ProductsDatabase,
    private val clientsDatabase: ClientsDatabase,
    private val suppliersDatabase: SuppliersDatabase,
    private val recordsDatabase: RecordsDatabase,
    private val businessDatabase: BusinessDatabase,
    private val productsDao: ProductsDao
):IBackupRepository {

    private val storage =  Firebase.storage.reference
    private val auth = FirebaseAuth.getInstance()

    //hacer esta funcion sin suspend y con state flow para poder mostrar el progreso? idk
    //ponerla linda que esta un desastre
    override suspend fun createBackupForBusiness(businessID: String, paths: List<String>):SimpleResult {
        return try {
            closeDatabasesForBackup()
            val databasesFiles = paths.map { File(it) }
            val dbRef = storage.child("users/${auth.currentUser?.uid}/backup/$businessID.zip")
            zipDatabases(databasesFiles, context.filesDir.path, "$businessID.zip")
            val zipFile = File(context.filesDir.path + "/$businessID.zip")
            withContext(Dispatchers.IO) {
                val stream = FileInputStream(zipFile)
                dbRef.putStream(stream).await()
            }
            zipFile.delete()
            SimpleResult.Success
        }
        catch (e: Exception){
            SimpleResult.Failure
        }
    }


    override suspend fun restoreBackupForBusiness(businessID: String, paths: List<String>): SimpleResult  {
        return try {
            closeDatabasesForBackup()
            val dbRef = storage.child("users/${auth.currentUser?.uid}/backup/$businessID.zip")

            val localFile = File(context.filesDir.path + "/backup/$businessID", "$businessID.zip")

            if (!localFile.exists()) localFile.parentFile?.mkdirs()
            dbRef.getFile(localFile).await()

            withContext(Dispatchers.IO){
                WZip().unzip(localFile, localFile.parentFile!! ,"bitMapZipper" , object : WZipCallback{

                    override fun onStarted(identifier: String?) {

                    }

                    override fun onZipCompleted(zipFile: File?, identifier: String?) {
                    }

                    override fun onUnzipCompleted(identifier: String?) {
                        paths.forEach {
                            copyFile(
                                File(localFile.parentFile!!.path + "/" + it.substringAfterLast("/")).inputStream(),
                                File(it).outputStream()
                            )
                            File(localFile.parentFile!!.path + "/" + it.substringAfterLast("/")).delete()
                        }
                        localFile.delete()
                    }

                    override fun onError(throwable: Throwable?, identifier: String?) {

                    }
                })
            }

            SimpleResult.Success
        }catch (e: Exception){
            println(e.localizedMessage)
            SimpleResult.Failure
        }
    }

    private fun copyFile(fromFile: FileInputStream, toFile: FileOutputStream) {
        var fromChannel: FileChannel? = null
        var toChannel: FileChannel? = null
        try {
            fromChannel = fromFile.channel
            toChannel = toFile.channel
            fromChannel.transferTo(0, fromChannel.size(), toChannel)
        } finally {
            try {
                fromChannel?.close()
            } finally {
                toChannel?.close()
            }
        }
    }


    private fun closeDatabasesForBackup(){
        productsDatabase.close()
        clientsDatabase.close()
        suppliersDatabase.close()
        recordsDatabase.close()
        businessDatabase.close()
    }
}