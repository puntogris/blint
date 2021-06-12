package com.puntogris.blint.ui.product

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.Timestamp
import com.puntogris.blint.data.local.dao.*
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductViewModel @ViewModelInject constructor(
    private val productsDao: ProductsDao,
    private val ordersDao: OrdersDao,
    private val suppliersDao: SuppliersDao,
    private val userRepository: UserRepository,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao,
    private val categoriesDao: CategoriesDao
):ViewModel() {

    var viewsLoaded = false

    private val _productImage = MutableLiveData(hashMapOf("uri" to "", "path" to ""))
    val productImage: LiveData<HashMap<String, String>> = _productImage

    private val suppliers = MutableLiveData(listOf<Int>())
    private val categories = MutableLiveData(listOf<Int>())

    fun updateSuppliers(suppliers:List<Int>){
        this.suppliers.value = suppliers
    }

    fun updateCategories(categories:List<Int>){
        this.categories.value = categories
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
            ordersDao.getProductRecordsPaged(productID)
        }.flow
    }

    suspend fun saveProductDatabase(){
        _currentProduct.value.businessId = usersDao.getUser().currentBusinessId
        val productId = productsDao.insert(_currentProduct.value)
        saveRecordToDatabase(productId)
        if (_currentProduct.value.productId == 0) statisticsDao.incrementTotalProducts()
        saveProductSuppliersCrossRef(productId.toInt())
        saveProductCategoryCrossRef(productId.toInt())
    }

    fun updateCurrentProductBarcode(barcode: String){
        _currentProduct.value.barcode = barcode
    }

    private suspend fun saveRecordToDatabase(productID: Long){
        val record = Record(
            type = "IN",
            amount = _currentProduct.value.amount,
            productId = productID.toInt(),
            productName = _currentProduct.value.name,
            timestamp = Timestamp.now(),
            author = userRepository.getCurrentUser()?.email.toString(),
            businessId = usersDao.getUser().currentBusinessId
        )
        ordersDao.insert(record)
    }

    private suspend fun saveProductSuppliersCrossRef(productId: Int){
        suppliers.value?.map {
            ProductSupplierCrossRef(productId, it)
        }?.let {
            productsDao.insertProductSupplierCrossRef(it)
        }
    }

    private suspend fun saveProductCategoryCrossRef(productId: Int){
        categories.value?.map {
            ProductCategoryCrossRef(productId, it)
        }?.let {
            productsDao.insertProductCategoriesCrossRef(it)
        }
    }

    fun deleteProductDatabase(id: Int){
        viewModelScope.launch {
            productsDao.delete(id)
            statisticsDao.decrementTotalProducts()
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

    suspend fun getProductWithSuppliersCategories(id: Int) = productsDao.getProductWithSuppliersCategories(id)

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

    suspend fun getAllCategories() = categoriesDao.getAllCategories()
    fun getAllCategoriesFlow() = categoriesDao.getAllCategoriesFlow()
    suspend fun deleteCategory(category: Category){
        categoriesDao.deleteCategory(category)
    }

    suspend fun insertCategory(name: String){
        categoriesDao.insert(Category(name = name, businessId = usersDao.getUser().currentBusinessId))
    }
}