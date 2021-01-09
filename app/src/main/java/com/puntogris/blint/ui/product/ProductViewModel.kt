package com.puntogris.blint.ui.product

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.ColumnInfo
import com.nguyenhoanglam.imagepicker.model.Image
import com.puntogris.blint.data.local.products.ProductsDao
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Supplier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProductViewModel @ViewModelInject constructor(
    private val productsDao: ProductsDao
):ViewModel() {

    private val _productImage = MutableLiveData(hashMapOf("uri" to "", "path" to ""))
    val productImage: LiveData<HashMap<String, String>> = _productImage

    private val _currentProduct = MutableStateFlow(Product())
    val currentProduct :LiveData<Product> = _currentProduct.asLiveData()

    fun saveProduct(){
        viewModelScope.launch {
            productsDao.insert(_currentProduct.value)
        }
    }

    fun updateCurrentProductData(product: Product){
        product.id = _currentProduct.value.id
        _currentProduct.value = product
    }

    fun setCurrentProductData(product: Product){
        _currentProduct.value = product
    }

    fun updateProductImage(imageMap: HashMap<String, String>){
        _productImage.value = imageMap
        _currentProduct.value.image = imageMap
    }

    fun removeCurrentImage(){
        val imageMap = hashMapOf("uri" to "", "path" to "")
        _productImage.value = imageMap
        _currentProduct.value.image = imageMap
    }

    fun getAllProducts(): Flow<PagingData<Product>> {
        return Pager(PagingConfig(
            pageSize = 30,
            enablePlaceholders = true,
            maxSize = 200
        )){
            productsDao.getAllPaged()
        }.flow
    }

}