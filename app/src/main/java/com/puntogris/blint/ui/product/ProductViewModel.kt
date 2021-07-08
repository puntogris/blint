package com.puntogris.blint.ui.product

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.data.local.dao.*
import com.puntogris.blint.data.repo.ProductRepository
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.SearchText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    private var imageChanged = false
    private val _productImage = MutableLiveData("")
    val productImage: LiveData<String> = _productImage
    private val _currentProduct = MutableStateFlow(ProductWithSuppliersCategories())
    val currentProduct: LiveData<ProductWithSuppliersCategories> = _currentProduct.asLiveData()

    fun updateSuppliers(suppliers: List<FirestoreSupplier>){
        if (!_currentProduct.value.suppliers.isNullOrEmpty()){
            _currentProduct.value.suppliers =
                _currentProduct.value.suppliers!!.union(suppliers).toList()
        }else _currentProduct.value.suppliers = suppliers
    }

    fun updateCategories(categories: List<FirestoreCategory>){
        if (!_currentProduct.value.categories.isNullOrEmpty()){
            _currentProduct.value.categories =
                _currentProduct.value.categories!!.union(categories).toList()
        }else _currentProduct.value.categories = categories
    }

    @ExperimentalCoroutinesApi
    suspend fun getProductCategories() = productRepository.getProductCategoriesDatabase()

    fun removeCurrentImage(){
        val image = ""
        _productImage.value = image
        _currentProduct.value.product.image = image
    }

    fun updateCurrentProductBarcode(barcode: String){
        _currentProduct.value.product.barcode = barcode
    }

    fun updateProductData(product: Product){
        product.productId = _currentProduct.value.product.productId
        _currentProduct.value.product = product
    }

    fun setProductData(product: ProductWithSuppliersCategories){
        _currentProduct.value = product
    }

    fun updateProductImage(image: String){
        imageChanged = true
        _productImage.value = image
        _currentProduct.value.product.image = image
    }

    suspend fun getProductRecords(productId:String) =
        productRepository.getProductRecordsPagingDataFlow(productId).cachedIn(viewModelScope)

    suspend fun saveProductDatabase() =
        productRepository.saveProductDatabase(_currentProduct.value, imageChanged)

    suspend fun deleteProductDatabase(productId: String) = productRepository.deleteProductDatabase(productId)

    suspend fun getProductsPaging() = productRepository.getProductsPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getProductWithSuppliersCategories(id: String) =
        productsDao.getProductWithSuppliersCategories(id)

    suspend fun getProductWithName(search: SearchText) =
        productRepository.getProductsWithNamePagingDataFlow(search)

    suspend fun getAllSuppliers() = suppliersDao.getAllSuppliers()

    suspend fun getAllCategories() = categoriesDao.getAllCategories()

    suspend fun deleteCategory(categories: List<Category>) =
        productRepository.deleteProductCategoryDatabase(categories)

    suspend fun saveCategoryDatabase(name: String) =
        productRepository.saveProductCategoryDatabase(Category(name = name))

    suspend fun updateCategoryDatabase(category: Category) =
        productRepository.updateProductCategoryDatabase(category)

    suspend fun getCategoriesWithName(name:String) =
        productRepository.getCategoriesWithNameDatabase(name)

    suspend fun getSuppliersWithName(name: String) =
        productRepository.getSuppliersWithNameDatabase(name)

}