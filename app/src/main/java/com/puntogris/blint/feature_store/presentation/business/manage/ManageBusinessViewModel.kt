package com.puntogris.blint.feature_store.presentation.business.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.domain.model.Business
import com.puntogris.blint.feature_store.domain.repository.BusinessRepository
import com.puntogris.blint.feature_store.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@HiltViewModel
class ManageBusinessViewModel @Inject constructor(
    businessRepository: BusinessRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val currentBusiness = userRepository.getUserFlow()

    private val userBusinesses = businessRepository.getBusinessesFlow()

    val businesses = combine(userBusinesses, currentBusiness) { businesses, current ->
        businesses.map {
            SelectedBusiness(
                business = it,
                isSelected = it.businessId == current.currentBusinessId
            )
        }
    }

    suspend fun updateCurrentBusiness(business: Business): SimpleResult {
        return userRepository.updateCurrentBusiness(business.businessId)
    }
}

