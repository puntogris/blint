package com.puntogris.blint.feature_store.presentation.store.manage

import androidx.lifecycle.ViewModel
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

    private val currentStore = userRepository.getUserFlow()

    private val userStores = storeRepository.getStoresFlow()

    val storesFlow = combine(userStores, currentStore) { stores, current ->
        stores.map {
            SelectedStore(
                store = it,
                isSelected = it.storeId == current.currentStoreId
            )
        }
    }

    suspend fun updateCurrentStore(store: Store) = userRepository.updateCurrentStore(store.storeId)
}

