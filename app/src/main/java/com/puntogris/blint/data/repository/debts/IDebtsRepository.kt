package com.puntogris.blint.data.repository.debts

import androidx.paging.PagingData
import com.puntogris.blint.model.order.Debt
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IDebtsRepository {

    suspend fun saveDebt(debt: Debt): SimpleResult

    fun getDebtsPaged(): Flow<PagingData<Debt>>

    fun getLastTraderDebts(traderId: String): Flow<PagingData<Debt>>
}