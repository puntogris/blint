package com.puntogris.blint.data.repository.statistics

import androidx.lifecycle.LiveData
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Statistic
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.model.product.Product
import com.puntogris.blint.model.product.ProductRecordExcel
import com.puntogris.blint.utils.types.RepoResult

interface IStatisticRepository {

    fun getCurrentBusinessStatistics(): LiveData<Statistic>

    suspend fun getAllClients(): RepoResult<List<Client>>

    suspend fun getAllProducts(): RepoResult<List<Product>>

    suspend fun getAllSuppliers(): RepoResult<List<Supplier>>

    suspend fun getBusinessCounters(): RepoResult<Statistic>

    suspend fun getProductsReports(timeCode: String): RepoResult<List<ProductRecordExcel>>

//    suspend fun getClientsReports(timeCode:String, startTime:Long, endTime:Long): RepoResult<List<ClientRecordExcel>>

//    suspend fun getSuppliersReports(timeCode:String, startTime:Long, endTime:Long): RepoResult<List<SupplierRecordExcel>>
}