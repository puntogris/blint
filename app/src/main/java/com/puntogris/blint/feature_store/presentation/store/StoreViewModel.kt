package com.puntogris.blint.feature_store.presentation.store

import androidx.lifecycle.ViewModel
import com.puntogris.blint.feature_store.domain.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    suspend fun deleteBusiness(businessId: String) = repository.deleteStore(businessId)
}
