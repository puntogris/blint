package com.puntogris.blint.feature_store.presentation.store.register_store

import androidx.lifecycle.ViewModel
import com.puntogris.blint.feature_store.domain.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterStoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    fun registerNewBusiness(name: String) = storeRepository.registerStore(name)
}
