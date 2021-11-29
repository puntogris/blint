package com.puntogris.blint.feature_store.presentation.business.manage

import androidx.lifecycle.ViewModel
import com.puntogris.blint.feature_store.domain.repository.BusinessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageBusinessViewModel @Inject constructor(
    repository: BusinessRepository
) : ViewModel() {

    val businesses = repository.getBusinessFlow()
}