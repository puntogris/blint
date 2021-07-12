package com.puntogris.blint.data.repo.irepo

import androidx.paging.PagingData
import com.puntogris.blint.model.BusinessDebtsData
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Debt
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IDebtsRepository {
    suspend fun getLastTraderDebts(traderId: String): RepoResult<List<Debt>>
    suspend fun registerNewDebtDatabase(debt: Debt): SimpleResult
    suspend fun getBusinessDebtData(): BusinessDebtsData
    suspend fun getBusinessDebtsPagingDataFlow(): Flow<PagingData<Debt>>
    suspend fun getClientPagingDataFlow(): Flow<PagingData<Client>>
    suspend fun getSupplierPagingDataFlow(): Flow<PagingData<Supplier>>
}