package com.puntogris.blint.feature_store.domain.repository

import androidx.paging.PagingData
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.domain.model.order.Debt
import kotlinx.coroutines.flow.Flow

interface DebtsRepository {

    suspend fun saveDebt(debt: Debt): SimpleResult

    fun getDebtsPaged(): Flow<PagingData<Debt>>

    fun getLastTraderDebts(traderId: String): Flow<PagingData<Debt>>
}