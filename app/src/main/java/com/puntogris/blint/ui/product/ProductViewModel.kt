package com.puntogris.blint.ui.product

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.local.products.ProductsDao
import com.puntogris.blint.data.local.records.RecordsDao
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.Constants.STOCK_DECREASE
import com.puntogris.blint.utils.Constants.STOCK_INCREASE
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.abs

class ProductViewModel @ViewModelInject constructor(
    private val productsDao: ProductsDao,
    private val recordsDao: RecordsDao,

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
            amount = abs(initialAmount - _currentProduct.value.amount),
            product = _currentProduct.value.id
        )
        recordsDao.insert(record)
    }

    fun deleteProductDatabase(id: Int){
        viewModelScope.launch {
            productsDao.delete(id)
        }
    }

    fun updateProductData(product: Product){
        product.id = _currentProduct.value.id
        _currentProduct.value = product
    }

    fun setProductData(product: Product){
        _currentProduct.value = product
        initialAmount = product.amount
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

    suspend fun getProduct(id: Int) =
        productsDao.getProduct(id)

    fun getProductWithName(name: String): Flow<PagingData<Product>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            productsDao.getPagedSearch("%${name}%")
        }.flow
    }

    suspend fun getProductWithBarCode(barcode:String) =
        productsDao.getProductWithBarcode(barcode)

    fun getProductID() = _currentProduct.value.id

    fun setNewProductStock(amount: Int){
        viewModelScope.launch {
            productsDao.updateProductAmount(_currentProduct.value.id, amount )
        }
    }
}