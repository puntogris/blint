package com.puntogris.blint.ui.product.suppliers

import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.map
import com.puntogris.blint.domain.repository.SupplierRepository
import com.puntogris.blint.model.CheckableSupplier
import com.puntogris.blint.model.Supplier
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
