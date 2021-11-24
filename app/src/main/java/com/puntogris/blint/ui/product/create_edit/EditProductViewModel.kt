package com.puntogris.blint.ui.product.create_edit

import android.text.Editable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.puntogris.blint.domain.repository.ProductRepository
import com.puntogris.blint.model.Category
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.model.product.ProductWithDetails
import com.puntogris.blint.utils.types.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productWithDetails =
        savedStateHandle.get<ProductWithDetails>("productWithDetails") ?: ProductWithDetails()

    private val _currentProduct = MutableStateFlow(productWithDetails)
    val currentProduct = _currentProduct.asStateFlow()

    suspend fun saveProduct(): SimpleResult = productRepository.saveProduct(currentProduct.value)

    fun updateProductImage(image: String) {
        _currentProduct.value.product.image = image
    }

    fun updateProductSuppliers(suppliers: List<Supplier>) {
        _currentProduct.value.suppliers = suppliers
    }

    fun updateProductCategories(categories: List<Category>) {
        _currentProduct.value.categories = categories
    }

    fun updateProductBarcode(barcode: String) {
        _currentProduct.value.product.barcode = barcode
    }

    fun updateProductBarcode(editable: Editable) {
        _currentProduct.value.product.barcode = editable.toString()
    }

    fun updateProductName(editable: Editable) {
        _currentProduct.value.product.name = editable.toString()
    }

    fun updateProductDescription(editable: Editable) {
        _currentProduct.value.product.description = editable.toString()
    }

    fun updateProductBuyPrice(editable: Editable) {
        _currentProduct.value.product.buyPrice = editable.toString().toFloatOrNull() ?: 0F
    }

    fun updateProductSellPrice(editable: Editable) {
        _currentProduct.value.product.sellPrice = editable.toString().toFloatOrNull() ?: 0F
    }

    fun updateProductSuggestedPrice(editable: Editable) {
        _currentProduct.value.product.suggestedSellPrice = editable.toString().toFloatOrNull() ?: 0F
    }

    fun updateProductAmount(editable: Editable) {
        _currentProduct.value.product.amount = editable.toString().toIntOrNull() ?: 0
    }

    fun updateProductSku(editable: Editable) {
        _currentProduct.value.product.sku = editable.toString()
    }

    fun updateProductBrand(editable: Editable) {
        _currentProduct.value.product.brand = editable.toString()
    }

    fun updateProductSize(editable: Editable) {
        _currentProduct.value.product.size = editable.toString()
    }
}