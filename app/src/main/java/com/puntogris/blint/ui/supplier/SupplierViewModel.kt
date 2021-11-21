package com.puntogris.blint.ui.supplier

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.data.repository.supplier.SupplierRepository
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.utils.types.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SupplierViewModel @Inject constructor(
    private val supplierRepository: SupplierRepository
) : ViewModel() {

    private val _currentSupplier = MutableStateFlow(Supplier())
    val currentSupplier: LiveData<Supplier> = _currentSupplier.asLiveData()

    suspend fun deleteSupplierDatabase(supplierId: String) =
        supplierRepository.deleteSupplier(supplierId)

    suspend fun saveSupplierDatabase(): SimpleResult {
        return supplierRepository.saveSupplier(_currentSupplier.value.apply {
            companyName = companyName.lowercase()
        })
    }

    fun getSupplierRecords(supplierId: String) =
        supplierRepository.getSupplierRecordsPaged(supplierId).cachedIn(viewModelScope)

    fun updateSupplierData(supplier: Supplier) {
        supplier.supplierId = _currentSupplier.value.supplierId
        _currentSupplier.value = supplier
    }

    fun setSupplierData(supplier: Supplier) {
        _currentSupplier.value = supplier
    }
}