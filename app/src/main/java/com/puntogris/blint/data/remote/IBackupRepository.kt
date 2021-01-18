package com.puntogris.blint.data.remote

import com.puntogris.blint.utils.SimpleResult

interface IBackupRepository {
    suspend fun createBackupForBusiness(businessID:String, paths: List<String>): SimpleResult
    suspend fun restoreBackupForBusiness(businessID: String,  paths: List<String>): SimpleResult

}