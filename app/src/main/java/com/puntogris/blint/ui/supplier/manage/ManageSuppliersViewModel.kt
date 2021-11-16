package com.puntogris.blint.ui.supplier.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.data.repository.supplier.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageSuppliersViewModel @Inject constructor(
    private val supplierRepository: SupplierRepository
):ViewModel() {

    suspend fun getSuppliersPaging() =
        supplierRepository.getSupplierPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getSuppliersWithName(supplierName: String)
            = supplierRepository.getSupplierWithNamePagingDataFlow(supplierName).cachedIn(viewModelScope)
}