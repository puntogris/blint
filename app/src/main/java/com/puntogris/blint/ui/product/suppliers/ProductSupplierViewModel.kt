package com.puntogris.blint.ui.product.suppliers

import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.map
import com.puntogris.blint.data.repository.supplier.SupplierRepository
import com.puntogris.blint.model.FirestoreSupplier
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductSupplierViewModel @Inject constructor(
    private val repository: SupplierRepository
) : ViewModel() {

    private val query = MutableLiveData("")

    val suppliersLiveData = query.switchMap {
        repository.getSuppliersPaged(it).asLiveData()
    }.map {
        it.map { supp ->
            FirestoreSupplier(
                companyName = supp.companyName,
                supplierId = supp.supplierId
            )
        }
    }.cachedIn(viewModelScope)

    fun setQuery(query: String) {
        this.query.value = query
    }
}