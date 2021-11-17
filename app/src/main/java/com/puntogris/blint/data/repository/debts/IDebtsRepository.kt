package com.puntogris.blint.data.repository.debts

import androidx.paging.PagingData
import com.puntogris.blint.model.BusinessDebtsData
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Debt
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.utils.types.RepoResult
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IDebtsRepository {
    suspend fun getLastTraderDebts(traderId: Int): RepoResult<List<Debt>>
    suspend fun registerNewDebtDatabase(debt: Debt): SimpleResult
    suspend fun getBusinessDebtData(): BusinessDebtsData
    fun getBusinessDebtsPagingDataFlow(): Flow<PagingData<Debt>>
    fun getClientPagingDataFlow(): Flow<PagingData<Client>>
    fun getSupplierPagingDataFlow(): Flow<PagingData<Supplier>>
}