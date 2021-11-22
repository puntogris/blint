package com.puntogris.blint.ui.supplier.create_edit

import android.text.Editable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.repository.supplier.SupplierRepository
import com.puntogris.blint.model.Supplier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EditSupplierViewModel @Inject constructor(
    private val repository: SupplierRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val supplier = savedStateHandle.get<Supplier>("supplier") ?: Supplier()

    private val _currentSupplier = MutableStateFlow(supplier)
    val currentSupplier = _currentSupplier.asStateFlow()

    suspend fun saveSupplier() = repository.saveSupplier(_currentSupplier.value)

    fun updateSupplierCompanyName(editable: Editable) {
        _currentSupplier.value.companyName = editable.toString()
    }

    fun updateSupplierCompanyPhone(editable: Editable) {
        _currentSupplier.value.companyName = editable.toString()
    }

    fun updateSupplierAddress(editable: Editable) {
        _currentSupplier.value.address = editable.toString()
    }

    fun updateSupplierCompanyEmail(editable: Editable) {
        _currentSupplier.value.companyEmail = editable.toString()
    }

    fun updateSupplierCompanyPaymentInfo(editable: Editable) {
        _currentSupplier.value.companyPaymentInfo = editable.toString()
    }

    fun updateSupplierSellerEmail(editable: Editable) {
        _currentSupplier.value.sellerEmail = editable.toString()
    }

    fun updateSupplierSellerName(editable: Editable) {
        _currentSupplier.value.sellerName = editable.toString()
    }

    fun updateSupplierSellerPhone(editable: Editable) {
        _currentSupplier.value.sellerPhone = editable.toString()
    }

    fun updateSupplierNotes(editable: Editable) {
        _currentSupplier.value.notes = editable.toString()
    }
}