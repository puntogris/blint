package com.puntogris.blint.ui.create_product

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.ProductsDao
import com.puntogris.blint.model.Product

class CreateProductViewModel @ViewModelInject constructor(
    private val productsDao: ProductsDao
):ViewModel() {


    fun saveProduct(){
        //productsDao.insert()
    }
}