package com.puntogris.blint.feature_store.presentation.product.suppliers

import android.text.Editable
import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.map
import com.puntogris.blint.feature_store.domain.model.CheckableSupplier
import com.puntogris.blint.feature_store.domain.model.Supplier
import com.puntogris.blint.feature_store.domain.repository.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ProductSupplierViewModel @Inject constructor(
    private val repository: SupplierRepository,
    handle: SavedStateHandle
) : ViewModel() {

    private val query = MutableStateFlow("")

    private val initialSuppliers =
        ProductSupplierFragmentArgs.fromSavedStateHandle(handle).suppliers ?: emptyArray()

    private val initialSuppliersIds = initialSuppliers.map { it.supplierId }

    private val finalSuppliers = initialSuppliers.toMutableList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val suppliersFlow = query.flatMapLatest {
        repository.getSuppliersPaged(it)
    }.map {
        it.map { supplier ->
            CheckableSupplier(
                supplier = supplier,
                isChecked = supplier.supplierId in initialSuppliersIds
            )
        }
    }.cachedIn(viewModelScope)

    fun setQuery(editable: Editable) {
        query.value = editable.toString()
    }

    fun toggleSupplier(supplier: Supplier) {
        if (supplier in finalSuppliers) finalSuppliers.remove(supplier)
        else finalSuppliers.add(supplier)
    }

    fun getFinalSuppliers() = finalSuppliers
}
