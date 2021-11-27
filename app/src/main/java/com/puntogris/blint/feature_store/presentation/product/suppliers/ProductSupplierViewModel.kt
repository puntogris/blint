package com.puntogris.blint.feature_store.presentation.product.suppliers

import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.map
import com.puntogris.blint.feature_store.domain.model.CheckableSupplier
import com.puntogris.blint.feature_store.domain.model.Supplier
import com.puntogris.blint.feature_store.domain.repository.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductSupplierViewModel @Inject constructor(
    private val repository: SupplierRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val query = MutableLiveData("")

    private val initialSuppliers =
        savedStateHandle.get<Array<Supplier>>("suppliers") ?: emptyArray()

    private val initialSuppliersIds = initialSuppliers.map { it.supplierId }

    private val finalSuppliers = initialSuppliers.toMutableList()

    val suppliersLiveData = query.switchMap {
        repository.getSuppliersPaged(it).asLiveData()
    }.map {
        it.map { supplier ->
            CheckableSupplier(
                supplier = supplier,
                isChecked = supplier.supplierId in initialSuppliersIds
            )
        }
    }.cachedIn(viewModelScope)

    fun setQuery(query: String) {
        this.query.value = query
    }

    fun toggleSupplier(supplier: Supplier) {
        if (supplier in finalSuppliers) finalSuppliers.remove(supplier)
        else finalSuppliers.add(supplier)
    }

    fun getFinalSuppliers() = finalSuppliers
}
