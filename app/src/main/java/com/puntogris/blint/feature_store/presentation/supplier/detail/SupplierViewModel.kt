package com.puntogris.blint.feature_store.presentation.supplier.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.feature_store.domain.model.Supplier
import com.puntogris.blint.feature_store.domain.repository.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SupplierViewModel @Inject constructor(
    private val repository: SupplierRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val supplierArg = savedStateHandle.getLiveData<Supplier>("supplier").asFlow()
    private val supplierIdArg = savedStateHandle.getLiveData<String>("supplierId").asFlow()

    val currentSupplier = combine(supplierArg, supplierIdArg) { supplier, id ->
        supplier ?: repository.getSupplier(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Supplier())

    @ExperimentalCoroutinesApi
    val supplierRecords = currentSupplier.flatMapLatest {
        repository.getSupplierRecordsPaged(it.supplierId)
    }.cachedIn(viewModelScope)

    suspend fun deleteSupplier() = repository.deleteSupplier(currentSupplier.value.supplierId)
}