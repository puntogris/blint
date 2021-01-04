package com.puntogris.blint.ui.register.product

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyenhoanglam.imagepicker.model.Image
import com.puntogris.blint.data.local.products.ProductsDao
import com.puntogris.blint.model.Product
import kotlinx.coroutines.launch

class CreateProductViewModel @ViewModelInject constructor(
    private val productsDao: ProductsDao
):ViewModel() {

    private val _productImage = MutableLiveData<Image>()
    val productImage: LiveData<Image> = _productImage

    fun saveProduct(product: Product){
        viewModelScope.launch {
            productsDao.insert(product)
        }
    }

    fun updateProductImage(image: Image){
        _productImage.value = image
    }

}