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
import com.puntogris.blint.utils.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productsDao: ProductsDao,
    private val suppliersDao: SuppliersDao,
    private val categoriesDao: CategoriesDao,
    private val productRepository: ProductRepository
):ViewModel() {

    var viewsLoaded = false

    private val _productImage = MutableLiveData(hashMapOf("uri" to "", "path" to ""))
    val productImage: LiveData<HashMap<String, String>> = _productImage
    private val suppliers = MutableLiveData(listOf<String>())
    private val categories = MutableLiveData(listOf<String>())
    private val _currentProduct = MutableStateFlow(Product())
    val currentProduct :LiveData<Product> = _currentProduct.asLiveData()

    fun updateSuppliers(suppliers:List<String>){
        this.suppliers.value = suppliers
    }

    fun updateCategories(categories:List<String>){
        this.categories.value = categories
    }

    fun getAllCategoriesFlow() = categoriesDao.getAllCategoriesFlow()

    fun removeCurrentImage(){
        val imageMap = hashMapOf("uri" to "", "path" to "")
        _productImage.value = imageMap
        _currentProduct.value.image = imageMap
    }

    fun updateCurrentProductBarcode(barcode: String){
        _currentProduct.value.barcode = barcode
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

    suspend fun getProductRecords(productId:String) =
        productRepository.getProductRecordsPagingDataFlow(productId).cachedIn(viewModelScope)

    suspend fun saveProductDatabase() =
        productRepository.saveProductDatabase(_currentProduct.value, suppliers.value!!, categories.value!!)

    suspend fun deleteProductDatabase(productId: String) = productRepository.deleteProductDatabase(productId)

    suspend fun getProductsPaging() = productRepository.getProductsPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getProductWithSuppliersCategories(id: String) =
        productsDao.getProductWithSuppliersCategories(id)

    suspend fun getProductWithName(productName: String) =
        productRepository.getProductsWithNamePagingDataFlow(productName)

    suspend fun getAllSuppliers() = suppliersDao.getAllSuppliers()

    suspend fun getAllCategories() = categoriesDao.getAllCategories()

    suspend fun deleteCategory(category: Category) =
        productRepository.deleteProductCategoryDatabase(category)

    suspend fun insertCategory(name: String) =
        productRepository.saveProductCategoryDatabase(Category(name = name))

}