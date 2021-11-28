package com.puntogris.blint.feature_store.presentation.supplier.manage

import android.text.Editable
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.feature_store.domain.repository.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class ManageSuppliersViewModel @Inject constructor(
    private val supplierRepository: SupplierRepository
) : ViewModel() {

    private val query = MutableStateFlow("")

    @ExperimentalCoroutinesApi
    val suppliersFlow = query.flatMapLatest {
        supplierRepository.getSuppliersPaged(it)
    }.cachedIn(viewModelScope)

    fun setQuery(editable: Editable) {
        query.value = editable.toString()
    }
}