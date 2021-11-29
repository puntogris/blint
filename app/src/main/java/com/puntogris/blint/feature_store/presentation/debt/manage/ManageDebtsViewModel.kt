package com.puntogris.blint.feature_store.presentation.debt.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.feature_store.domain.repository.BusinessRepository
import com.puntogris.blint.feature_store.domain.repository.DebtsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageDebtsViewModel @Inject constructor(
    debtsRepository: DebtsRepository,
    businessRepository: BusinessRepository
) : ViewModel() {

    val currentBusiness = businessRepository.getCurrentBusinessFlow().asLiveData()

    val debtsFlow = debtsRepository.getDebtsPaged().cachedIn(viewModelScope)
}