package com.puntogris.blint.ui.operations

import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.data_source.local.dao.ProductsDao
import com.puntogris.blint.data.data_source.local.dao.SuppliersDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OperationsViewModel @Inject constructor(
    private val suppliersDao: SuppliersDao,
    private val productsDao: ProductsDao
) : ViewModel() {

    private var supplierId: String = ""

    suspend fun getAllSuppliers() = suppliersDao.getSuppliers()

    fun updateSupplierId(supplierId: String) {
        this.supplierId = supplierId
    }

    suspend fun saveChangesToDatabase(
        valueType: String,
        changeAmount: Float,
        isValueUp: Boolean,
        affectsBuyPrice: Boolean,
        affectsSellPrice: Boolean,
        affectsSuggestedPrice: Boolean
    ): Int {
        return productsDao.updateSupplierProductsPrices(
            newPrice = changeAmount,
            supplierId = supplierId,
            valueType = valueType,
            isValueUp = isValueUp,
            affectsBuyPrice = affectsBuyPrice,
            affectsSellPrice = affectsSellPrice,
            affectsSuggestedPrice = affectsSuggestedPrice
        )
    }

}