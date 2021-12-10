package com.puntogris.blint.feature_store.presentation.supplier.create_edit

import android.net.Uri
import android.text.Editable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.puntogris.blint.common.framework.ContactsHelper
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.feature_store.domain.model.Supplier
import com.puntogris.blint.feature_store.domain.repository.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EditSupplierViewModel @Inject constructor(
    private val repository: SupplierRepository,
    private val savedStateHandle: SavedStateHandle,
    private val contactsHelper: ContactsHelper
) : ViewModel() {

    private val _currentSupplier = savedStateHandle.getLiveData<Supplier>("supplier")
        .asFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Supplier())

    val currentSupplier: StateFlow<Supplier> = _currentSupplier

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

    fun setContact(uri: Uri, code: Int) {
        contactsHelper.getContact(uri)?.let {
            val supplier = if (code == Constants.COMPANY_CONTACT) {
                _currentSupplier.value.copy(
                    companyName = it.name,
                    companyPhone = it.phone,
                    companyEmail = it.email,
                    address = it.address
                )
            } else {
                _currentSupplier.value.copy(
                    sellerName = it.name,
                    sellerEmail = it.email,
                    sellerPhone = it.phone
                )
            }
            savedStateHandle.set("supplier", supplier)
        }
    }
}