package com.puntogris.blint.ui.product

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.ColumnInfo
import com.nguyenhoanglam.imagepicker.model.Image
import com.puntogris.blint.data.local.products.ProductsDao
import com.puntogris.blint.data.local.records.RecordsDao
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Record
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.utils.Constants.STOCK_DECREASE
import com.puntogris.blint.utils.Constants.STOCK_INCREASE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.abs

class ProductViewModel @ViewModelInject constructor(
    private val productsDao: ProductsDao,
    private val recordsDao: RecordsDao
):ViewModel() {

    private var initialAmount = 0

    var viewsLoaded = false

    private val _productImage = MutableLiveData(hashMapOf("uri" to "", "path" to ""))
    val productImage: LiveData<HashMap<String, String>> = _productImage

    private val _currentProduct = MutableStateFlow(Product())
    val currentProduct :LiveData<Product> = _currentProduct.asLiveData()

    fun saveProductDatabase(){
        viewModelScope.launch {
            productsDao.insert(_currentProduct.value)
            saveRecordToDatabase()
        }
    }

    private suspend fun saveRecordToDatabase(){
        val type = if (initialAmount < _currentProduct.value.amount) STOCK_INCREASE else STOCK_DECREASE
        val record = Record(
            type = type,
            amount = abs(initialAmount - _currentProduct.value.amount).toInt(),
            product = _currentProduct.value.id
        )
        recordsDao.insert(record)
    }

    fun deleteProductDatabase(){
        viewModelScope.launch {
            productsDao.delete(_currentProduct.value)
        }
    }

    fun updateProductData(product: Product){
        product.id = _currentProduct.value.id
        _currentProduct.value = product
    }

    fun setProductData(product: Product){
        _currentProduct.value = product
        initialAmount = product.amount.toInt()
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

    fun getProductRecords(): Flow<PagingData<Record>> {
        return Pager(PagingConfig(
            pageSize = 30,
            enablePlaceholders = true,
            maxSize = 200
        )){
            recordsDao.getProductRecordsPaged(_currentProduct.value.id)
        }.flow
    }

}