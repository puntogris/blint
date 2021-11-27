package com.puntogris.blint.feature_store.presentation.debt.trader_debts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.feature_store.domain.model.SimpleDebt
import com.puntogris.blint.feature_store.domain.repository.ClientRepository
import com.puntogris.blint.feature_store.domain.repository.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ShowDebtsViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val supplierRepository: SupplierRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val debtType = savedStateHandle.get<String>("debtType") ?: ""

    fun getDebtsFlow(): Flow<PagingData<SimpleDebt>> {
        return when (debtType) {
            Constants.CLIENT -> clientRepository.getClientsPaged().map { pagingData ->
                pagingData.map {
                    SimpleDebt(it.name, it.debt, it.clientId)
                }
            }
            else -> supplierRepository.getSuppliersPaged().map { pagingData ->
                pagingData.map {
                    SimpleDebt(it.companyName, it.debt, it.supplierId)
                }
            }
        }.cachedIn(viewModelScope)
    }
}