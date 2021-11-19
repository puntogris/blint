package com.puntogris.blint.ui.supplier.manage

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.data.repository.supplier.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageSuppliersViewModel @Inject constructor(
    private val supplierRepository: SupplierRepository
) : ViewModel() {

    private val query = MutableLiveData("")

    val suppliersLiveData = query.switchMap {
        supplierRepository.getSuppliersPaged().asLiveData()
    }.cachedIn(viewModelScope)

    fun setQuery(query: String) {
        this.query.value = query
    }
}