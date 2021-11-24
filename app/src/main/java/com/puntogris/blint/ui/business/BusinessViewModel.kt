package com.puntogris.blint.ui.business

import androidx.lifecycle.ViewModel
import com.puntogris.blint.domain.repository.BusinessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BusinessViewModel @Inject constructor(
    private val businessRepository: BusinessRepository
) : ViewModel() {

    suspend fun deleteBusiness(businessId: String) = businessRepository.deleteBusiness(businessId)
}