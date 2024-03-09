package com.puntogris.blint.feature_store.presentation.debt.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.feature_store.domain.repository.DebtRepository
import com.puntogris.blint.feature_store.domain.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageDebtsViewModel @Inject constructor(
    debtRepository: DebtRepository,
    storeRepository: StoreRepository
) : ViewModel() {

    val currentBusiness = storeRepository.getCurrentStoreFlow()

    val debtsFlow = debtRepository.getDebtsPaged().cachedIn(viewModelScope)
}
