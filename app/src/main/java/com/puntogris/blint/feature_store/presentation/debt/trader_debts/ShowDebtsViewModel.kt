package com.puntogris.blint.feature_store.presentation.debt.trader_debts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.feature_store.domain.model.SimpleDebt
import com.puntogris.blint.feature_store.domain.repository.ClientRepository
import com.puntogris.blint.feature_store.domain.repository.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ShowDebtsViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val supplierRepository: SupplierRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val debtType = savedStateHandle.getLiveData<Int>("debtType")
        .asFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    @OptIn(ExperimentalCoroutinesApi::class)
    val debtsFlow = debtType.flatMapLatest { type ->
        when (type) {
            Constants.CLIENT_DEBT -> clientRepository.getClientsPaged().map { pagingData ->
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