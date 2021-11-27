package com.puntogris.blint.feature_store.presentation.business.register_business

import androidx.lifecycle.ViewModel
import com.puntogris.blint.feature_store.domain.repository.BusinessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterBusinessViewModel @Inject constructor(
    private val businessRepository: BusinessRepository
) : ViewModel() {

    fun registerNewBusiness(name: String) = businessRepository.registerBusiness(name)
}