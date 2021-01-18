package com.puntogris.blint.data.remote

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.puntogris.blint.data.local.businesses.BusinessDatabase
import com.puntogris.blint.data.local.clients.ClientsDatabase
import com.puntogris.blint.data.local.products.ProductsDatabase
import com.puntogris.blint.data.local.records.RecordsDatabase
import com.puntogris.blint.data.local.suppliers.SuppliersDatabase
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.Util
import com.puntogris.blint.utils.Util.zipDatabases
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
    private val productsDatabase: ProductsDatabase,
    private val clientsDatabase: ClientsDatabase,
    private val suppliersDatabase: SuppliersDatabase,
    private val recordsDatabase: RecordsDatabase,
    private val businessDatabase: BusinessDatabase
):IBackupRepository {

    private val storage =  Firebase.storage.reference
    private val auth = FirebaseAuth.getInstance()

    //hacer esta funcion sin suspend y con state flow para poder mostrar el progreso? idk
    //ponerla linda que esta un desastre
    override suspend fun createBackupForBusiness(businessID: String ,paths: List<String>):SimpleResult {
        return try {
            productsDatabase.close()
            clientsDatabase.close()
            suppliersDatabase.close()
            recordsDatabase.close()
            businessDatabase.close()
            val databasesFiles = paths.map { File(it) }
            val dbRef = storage.child("users/${auth.currentUser?.uid}/backup/$businessID.zip")
            zipDatabases(databasesFiles, context.filesDir.path, "$businessID.zip")
            withContext(Dispatchers.IO) {
                val stream = FileInputStream(File(context.filesDir.path +"/$businessID.zip"))
                dbRef.putStream(stream).await()
            }
            SimpleResult.Success
        }
        catch (e: Exception){
            SimpleResult.Failure
        }
    }

    override suspend fun restoreBackupForBusiness(): SimpleResult {
        return try {
            productsDatabase.close()
            val dbRef = storage.child("users/${auth.currentUser?.uid}/backup/products_table.db")
            val localFile = File(context.filesDir,"products_table.db")
            //if (!localFile.exists()) localFile.mkdirs()
            dbRef.getFile(localFile).await()
            copyFile(localFile.inputStream(), File("/data/user/0/com.puntogris.blint/databases/products_table").outputStream())

            SimpleResult.Success
        }catch (e: java.lang.Exception){
            SimpleResult.Failure
        }
    }

    fun copyFile(fromFile: FileInputStream, toFile: FileOutputStream) {
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
}