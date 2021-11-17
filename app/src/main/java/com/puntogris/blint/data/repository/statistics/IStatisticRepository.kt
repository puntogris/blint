package com.puntogris.blint.data.repository.statistics

import com.puntogris.blint.model.*
import com.puntogris.blint.utils.types.RepoResult

interface IStatisticRepository {
    suspend fun getAllClients(): RepoResult<List<Client>>
    suspend fun getAllProducts(): RepoResult<List<Product>>
    suspend fun getAllSuppliers(): RepoResult<List<Supplier>>
    suspend fun getBusinessCounters(): RepoResult<BusinessCounters>
    suspend fun getProductsReports(timeCode:String): RepoResult<List<ProductRecordExcel>>
//    suspend fun getClientsReports(timeCode:String, startTime:Long, endTime:Long): RepoResult<List<ClientRecordExcel>>
//    suspend fun getSuppliersReports(timeCode:String, startTime:Long, endTime:Long): RepoResult<List<SupplierRecordExcel>>
}