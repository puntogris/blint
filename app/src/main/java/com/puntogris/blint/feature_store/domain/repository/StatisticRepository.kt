package com.puntogris.blint.feature_store.domain.repository

import androidx.lifecycle.LiveData
import com.puntogris.blint.common.utils.types.RepoResult
import com.puntogris.blint.feature_store.domain.model.Client
import com.puntogris.blint.feature_store.domain.model.Statistic
import com.puntogris.blint.feature_store.domain.model.Supplier
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.domain.model.product.ProductRecordExcel

interface StatisticRepository {

    fun getCurrentBusinessStatistics(): LiveData<Statistic>

    suspend fun getAllClients(): RepoResult<List<Client>>

    suspend fun getAllProducts(): RepoResult<List<Product>>

    suspend fun getAllSuppliers(): RepoResult<List<Supplier>>

    suspend fun getBusinessCounters(): RepoResult<Statistic>

    suspend fun getProductsReports(timeCode: String): RepoResult<List<ProductRecordExcel>>

//    suspend fun getClientsReports(timeCode:String, startTime:Long, endTime:Long): RepoResult<List<ClientRecordExcel>>

//    suspend fun getSuppliersReports(timeCode:String, startTime:Long, endTime:Long): RepoResult<List<SupplierRecordExcel>>
}