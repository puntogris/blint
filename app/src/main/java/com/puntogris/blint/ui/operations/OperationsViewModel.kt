package com.puntogris.blint.ui.operations

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.Barcode
import com.puntogris.blint.data.local.dao.ProductsDao
import com.puntogris.blint.data.local.dao.SuppliersDao

class OperationsViewModel @ViewModelInject constructor(
    private val suppliersDao: SuppliersDao,
    private val productsDao: ProductsDao
): ViewModel() {

    private var supplierId: Int = 0

    suspend fun getAllSuppliers() = suppliersDao.getAllSuppliers()

    fun updateSupplierId(supplierId: Int){
        this.supplierId = supplierId
    }

    suspend fun saveChangesToDatabase(
        valueType: String,
        changeAmount: Float,
        isValueUp: Boolean,
        affectsBuyPrice : Boolean,
        affectsSellPrice: Boolean,
        affectsSuggestedPrice: Boolean):Int
    {
        return productsDao.updateSupplierProductsPrices(
            newPrice = changeAmount,
            supplierId = supplierId,
            valueType = valueType,
            isValueUp = isValueUp,
            affectsBuyPrice = affectsBuyPrice,
            affectsSellPrice = affectsSellPrice,
            affectsSuggestedPrice = affectsSuggestedPrice)
    }

}