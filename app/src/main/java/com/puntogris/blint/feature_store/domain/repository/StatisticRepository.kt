package com.puntogris.blint.feature_store.domain.repository

import android.net.Uri
import com.puntogris.blint.common.utils.types.SimpleResult

interface StatisticRepository {

    suspend fun saveClientListExcel(uri: Uri): SimpleResult

    suspend fun saveSupplierListExcel(uri: Uri): SimpleResult

    suspend fun saveProductListExcel(uri: Uri): SimpleResult

    suspend fun saveClientsRecords(days: Int, uri: Uri): SimpleResult

    suspend fun saveSuppliersRecords(days: Int, uri: Uri): SimpleResult

    suspend fun saveProductsRecords(days: Int, uri: Uri): SimpleResult
}