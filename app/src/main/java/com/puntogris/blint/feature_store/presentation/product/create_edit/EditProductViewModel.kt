package com.puntogris.blint.feature_store.presentation.product.create_edit

import android.text.Editable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.puntogris.blint.feature_store.domain.model.Category
import com.puntogris.blint.feature_store.domain.model.Supplier
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails
import com.puntogris.blint.feature_store.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    handle: SavedStateHandle
) : ViewModel() {

    private val _currentProduct = handle.getLiveData<ProductWithDetails>("productWithDetails")
        .asFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ProductWithDetails())

    val currentProduct: StateFlow<ProductWithDetails> = _currentProduct

    suspend fun saveProduct() = productRepository.saveProduct(currentProduct.value)

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
        val newAmount = editable.toString().toIntOrNull() ?: 0
        _currentProduct.value.product.apply {
            amount = newAmount
            historicInStock = newAmount
        }
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

