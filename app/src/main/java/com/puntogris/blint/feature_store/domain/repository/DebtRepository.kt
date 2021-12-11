package com.puntogris.blint.feature_store.domain.repository

import androidx.paging.PagingData
import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.feature_store.domain.model.order.Debt
import kotlinx.coroutines.flow.Flow

interface DebtRepository {

    suspend fun saveDebt(debt: Debt): SimpleResource

    fun getDebtsPaged(): Flow<PagingData<Debt>>

    fun getLastTraderDebts(traderId: String): Flow<PagingData<Debt>>
}