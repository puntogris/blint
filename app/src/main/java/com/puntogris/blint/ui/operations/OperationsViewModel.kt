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


    suspend fun getAllSuppliers() = suppliersDao.getAllSuppliers()

    suspend fun saveChangesToDatabase(
        valueType: String,
        changeAmount:Float,
        isValueUp: Boolean,
        affectsBuyPrice : Boolean,
        afffectsSellPrice:Boolean,
        affectsSuggetedPrice:Boolean )
    {
        //productsDao.updateProduct("1f")
    }


}