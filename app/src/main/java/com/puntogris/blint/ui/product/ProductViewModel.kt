package com.puntogris.blint.ui.product

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.Timestamp
import com.puntogris.blint.data.local.dao.ProductsDao
import com.puntogris.blint.data.local.dao.RecordsDao
import com.puntogris.blint.data.local.dao.SuppliersDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.ProductSupplierCrossRef
import com.puntogris.blint.model.Record
import com.puntogris.blint.ui.SharedPref
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductViewModel @ViewModelInject constructor(
    private val productsDao: ProductsDao,
    private val recordsDao: RecordsDao,
    private val suppliersDao: SuppliersDao,
    private val userRepository: UserRepository,
    private val usersDao: UsersDao
):ViewModel() {

    var viewsLoaded = false

    private val _productImage = MutableLiveData(hashMapOf("uri" to "", "path" to ""))
    val productImage: LiveData<HashMap<String, String>> = _productImage

    private val _suppliers = MutableLiveData(listOf<Int>())
    val suppliers :LiveData<List<Int>> = _suppliers

    fun updateSuppliers(suppliers:List<Int>){
        _suppliers.value = suppliers
    }

    private val _currentProduct = MutableStateFlow(Product())
    val currentProduct :LiveData<Product> = _currentProduct.asLiveData()

    fun getProductRecords(productID:Int): Flow<PagingData<Record>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ){
            recordsDao.getProductRecordsPaged(productID)
        }.flow
    }

    suspend fun saveProductDatabase(){
        _currentProduct.value.businessId = usersDao.getUser().currentBusinessId
        val productID = productsDao.insert(_currentProduct.value)
        saveRecordToDatabase(productID)
        saveProductSuppliersCrossRef(productID.toInt())
    }

    fun updateCurrentProductBarcode(barcode: String){
        _currentProduct.value.barcode = barcode
    }

    private suspend fun saveRecordToDatabase(productID: Long){
        val record = Record(
            type = "IN",
            amount = _currentProduct.value.amount,
            productID = productID.toInt(),
            productName = _currentProduct.value.name,
            timestamp = Timestamp.now(),
            author = userRepository.getCurrentUser()?.email.toString(),
            businessId = usersDao.getUser().currentBusinessId
        )
        recordsDao.insert(record)
    }

    private suspend fun saveProductSuppliersCrossRef(productId: Int){
        _suppliers.value?.map {
            ProductSupplierCrossRef(productId, it)
        }?.let {
            productsDao.insertProductSupplierCrossRef(it)
        }
    }

    fun deleteProductDatabase(id: Int){
        viewModelScope.launch {
            productsDao.delete(id)
        }
    }

    fun updateProductData(product: Product){
        product.productId = _currentProduct.value.productId
        _currentProduct.value = product
    }

    fun setProductData(product: Product){
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

    suspend fun getProduct(id: Int) = productsDao.getProduct(id)

    suspend fun getProductWithSuppliers(id: Int) = productsDao.getProductWithSuppliers(id)

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

    suspend fun getAllSuppliers() = suppliersDao.getAllSuppliers()

}