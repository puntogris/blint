package com.puntogris.blint.ui.business.register_business

import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.repo.business.BusinessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterBusinessViewModel @Inject constructor(
    private val businessRepository: BusinessRepository
) :ViewModel(){

    suspend fun registerNewBusiness(name: String) = businessRepository.registerLocalBusiness(name)
}