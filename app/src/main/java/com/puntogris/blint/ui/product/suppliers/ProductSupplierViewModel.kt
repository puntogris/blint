package com.puntogris.blint.ui.product.suppliers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.puntogris.blint.data.repository.supplier.SupplierRepository
import com.puntogris.blint.model.FirestoreSupplier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ProductSupplierViewModel @Inject constructor(
    private val supplierRepository: SupplierRepository
) : ViewModel() {

    suspend fun getSuppliersPaging() =
        supplierRepository.getAllSuppliersPaged()
            .map {
                it.map { supp ->
                    FirestoreSupplier(
                        companyName = supp.companyName,
                        supplierId = supp.supplierId
                    )
                }
            }.cachedIn(viewModelScope)

    suspend fun getSuppliersWithName(supplierName: String) =
        supplierRepository.getSuppliersWithNamePaged(supplierName)
            .map {
                it.map { supp ->
                    FirestoreSupplier(
                        companyName = supp.companyName,
                        supplierId = supp.supplierId
                    )
                }
            }.cachedIn(viewModelScope)
}