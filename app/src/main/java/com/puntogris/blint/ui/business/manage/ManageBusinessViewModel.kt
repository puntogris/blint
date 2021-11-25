package com.puntogris.blint.ui.business.manage

import androidx.lifecycle.ViewModel
import com.puntogris.blint.domain.repository.BusinessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageBusinessViewModel @Inject constructor(
    repository: BusinessRepository
) : ViewModel() {

    val businesses = repository.getBusinessFlow()

}