package com.puntogris.blint.data.repo.imp

import com.puntogris.blint.model.BusinessCounters
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.utils.RepoResult

interface IStatisticRepository {
    suspend fun getAllClients(): RepoResult<List<Client>>
    suspend fun getAllProducts(): RepoResult<List<Product>>
    suspend fun getAllSuppliers(): RepoResult<List<Supplier>>
    suspend fun getBusinessCounters(): RepoResult<BusinessCounters>
}