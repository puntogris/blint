package com.puntogris.blint.ui.debt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.puntogris.blint.data.repository.debts.DebtsRepository
import com.puntogris.blint.model.Debt
import com.puntogris.blint.model.SimpleDebt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DebtViewModel @Inject constructor(
    private val debtsRepository: DebtsRepository
    ): ViewModel() {

    val businessDebts = liveData { emit(debtsRepository.getBusinessDebtData()) }

    suspend fun registerNewDebt(debt:Debt) = debtsRepository.registerNewDebtDatabase(debt)

    suspend fun getLastDebts(traderId: String) = debtsRepository.getLastTraderDebts(traderId)

    suspend fun getAllDebts() =
        debtsRepository.getBusinessDebtsPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getAllClients() = debtsRepository.getClientPagingDataFlow()
        .map{ pagingData -> pagingData.map { SimpleDebt(it.name, it.debt, it.clientId) } }
        .cachedIn(viewModelScope)

    suspend fun getAllSuppliers() = debtsRepository.getSupplierPagingDataFlow()
        .map{ pagingData -> pagingData.map { SimpleDebt(it.companyName, it.debt, it.supplierId) } }
        .cachedIn(viewModelScope)
}