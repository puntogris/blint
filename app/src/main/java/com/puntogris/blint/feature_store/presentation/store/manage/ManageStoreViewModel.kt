package com.puntogris.blint.feature_store.presentation.store.manage

import androidx.lifecycle.ViewModel
import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.feature_store.domain.model.Store
import com.puntogris.blint.feature_store.domain.repository.StoreRepository
import com.puntogris.blint.feature_store.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class ManageStoreViewModel @Inject constructor(
    storeRepository: StoreRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val currentBusiness = userRepository.getUserFlow()

    private val userBusinesses = storeRepository.getStoresFlow()

    val businesses = combine(userBusinesses, currentBusiness) { businesses, current ->
        businesses.map {
            SelectedStore(
                store = it,
                isSelected = it.storeId == current.currentStoreId
            )
        }
    }

    suspend fun updateCurrentBusiness(store: Store): SimpleResource {
        return userRepository.updateCurrentBusiness(store.storeId)
    }
}

