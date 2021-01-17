package com.puntogris.blint.data.remote

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.puntogris.blint.utils.RepoResult
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

class BackupRepository @Inject constructor():IBackupRepository {
    private val storage =  Firebase.storage.reference


    override fun createBackupForBusiness(list: List<String>):RepoResult {
        return try {
            list.forEach {
                val dbRef = storage.child("products_table.db")
                val stream = FileInputStream(File(it))
                dbRef.putStream(stream)
            }
            RepoResult.Success
        }
        catch (e:Exception){
            RepoResult.Failure
        }
    }
}