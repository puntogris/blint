package com.puntogris.blint.ui.supplier.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.data.repository.supplier.SupplierRepository
import com.puntogris.blint.model.Supplier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SupplierViewModel @Inject constructor(
    private val repository: SupplierRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val supplier = savedStateHandle.get<Supplier>("supplier") ?: Supplier()

    private val _currentSupplier = MutableStateFlow(supplier)
    val currentSupplier = _currentSupplier.asStateFlow()

    suspend fun deleteSupplier(supplierId: String) = repository.deleteSupplier(supplierId)

    val supplierRecords =
        repository.getSupplierRecordsPaged(supplier.supplierId).cachedIn(viewModelScope)
}