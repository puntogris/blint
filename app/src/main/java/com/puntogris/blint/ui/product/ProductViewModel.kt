package com.puntogris.blint.ui.product

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.data.repository.products.ProductRepository
import com.puntogris.blint.model.Category
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.model.product.Product
import com.puntogris.blint.model.product.ProductWithDetails
import com.puntogris.blint.utils.types.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    var viewsLoaded = false

    private var imageChanged = false
    private val _productImage = MutableLiveData("")
    val productImage: LiveData<String> = _productImage
    private val _currentProduct = MutableStateFlow(ProductWithDetails())
    val currentProduct: LiveData<ProductWithDetails> = _currentProduct.asLiveData()

    fun updateSuppliers(suppliers: List<Supplier>) {
        _currentProduct.value.suppliers = suppliers
    }

    fun updateCategories(categories: List<Category>) {
        _currentProduct.value.categories = categories
    }

    fun removeCurrentImage() {
        val image = ""
        _productImage.value = image
        _currentProduct.value.product.image = image
    }

    fun updateCurrentProductBarcode(barcode: String) {
        _currentProduct.value.product.barcode = barcode
    }

    fun updateProductData(product: Product) {
        product.productId = _currentProduct.value.product.productId
        product.businessId = _currentProduct.value.product.businessId
        _currentProduct.value.product = product
    }

    fun setProductData(product: ProductWithDetails) {
        _currentProduct.value = product
    }

    fun updateProductImage(image: String) {
        imageChanged = true
        _productImage.value = image
        _currentProduct.value.product.image = image
    }

    suspend fun getProductRecords(productId: Int) =
        productRepository.getProductRecordsPaged(productId).cachedIn(viewModelScope)

    suspend fun saveProductDatabase(): SimpleResult {
        _currentProduct.value.apply {
            product.name = product.name.lowercase()
            product.sku = product.sku.uppercase()
        }
        return productRepository.saveProduct(_currentProduct.value)
    }

    suspend fun deleteProductDatabase(productId: Int) =
        productRepository.deleteProduct(productId)

}