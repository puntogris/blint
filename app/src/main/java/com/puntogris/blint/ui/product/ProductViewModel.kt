package com.puntogris.blint.ui.product

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.firebase.Timestamp
import com.puntogris.blint.data.local.dao.*
import com.puntogris.blint.data.remote.ProductRepository
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productsDao: ProductsDao,
    private val ordersDao: OrdersDao,
    private val suppliersDao: SuppliersDao,
    private val usersDao: UsersDao,
    private val categoriesDao: CategoriesDao,
    private val productRepository: ProductRepository
):ViewModel() {

    var viewsLoaded = false

    private val _productImage = MutableLiveData(hashMapOf("uri" to "", "path" to ""))
    val productImage: LiveData<HashMap<String, String>> = _productImage

    private val suppliers = MutableLiveData(listOf<String>())
    private val categories = MutableLiveData(listOf<String>())

    fun updateSuppliers(suppliers:List<String>){
        this.suppliers.value = suppliers
    }

    fun updateCategories(categories:List<String>){
        this.categories.value = categories
    }

    private val _currentProduct = MutableStateFlow(Product())
    val currentProduct :LiveData<Product> = _currentProduct.asLiveData()

    fun getProductRecords(productId:String): Flow<PagingData<Record>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ){
            ordersDao.getProductRecordsPaged(productId)
        }.flow
    }

    suspend fun saveProductDatabase() =
        productRepository.saveProductDatabase(_currentProduct.value, suppliers.value!!, categories.value!!)


    fun updateCurrentProductBarcode(barcode: String){
        _currentProduct.value.barcode = barcode
    }

    suspend fun deleteProductDatabase(productId: String) = productRepository.deleteProductDatabase(productId)

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

    suspend fun getProductsPaging() = productRepository.getProductsPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getProduct(id: String) = productsDao.getProduct(id)

    suspend fun getProductWithSuppliersCategories(id: String) = productsDao.getProductWithSuppliersCategories(id)

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