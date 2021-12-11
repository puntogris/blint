package com.puntogris.blint.feature_store.presentation.product.traders

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductTraderViewModel @Inject constructor(
    handle: SavedStateHandle
) : ViewModel() {

    private val query = MutableStateFlow("")

//    private val initialSuppliers =
//        ProductSupplierFragmentArgs.fromSavedStateHandle(handle).suppliers ?: emptyArray()
//
//    private val initialSuppliersIds = initialSuppliers.map { it.supplierId }
//
//    private val finalSuppliers = initialSuppliers.toMutableList()
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    val suppliersFlow = query.flatMapLatest {
//        repository.getSuppliersPaged(it)
//    }.map {
//        it.map { supplier ->
//            CheckableTrader(
//                supplier = supplier,
//                isChecked = supplier.supplierId in initialSuppliersIds
//            )
//        }
//    }.cachedIn(viewModelScope)
//
//    fun setQuery(editable: Editable) {
//        query.value = editable.toString()
//    }
//
//    fun toggleSupplier(supplier: Supplier) {
//        if (supplier in finalSuppliers) finalSuppliers.remove(supplier)
//        else finalSuppliers.add(supplier)
//    }
//
//    fun getFinalSuppliers() = finalSuppliers
}
