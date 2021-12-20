package com.puntogris.blint.feature_store.presentation.product.create_edit

import android.text.Editable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.puntogris.blint.common.utils.UUIDGenerator
import com.puntogris.blint.feature_store.domain.model.Category
import com.puntogris.blint.feature_store.domain.model.Trader
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails
import com.puntogris.blint.feature_store.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    handle: SavedStateHandle
) : ViewModel() {

    val currentProduct = handle.getLiveData<ProductWithDetails>("productWithDetails")
        .asFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ProductWithDetails())

    val productBarcode = MutableStateFlow("")
    val productImage = MutableStateFlow("")

    init {
        viewModelScope.launch {
            currentProduct.collect {
                productBarcode.value = it.product.barcode
                productImage.value = it.product.image
            }
        }
    }

    suspend fun saveProduct() = productRepository.saveProduct(currentProduct.value)

    fun updateProductBarcode(barcode: String? = null) {
        (barcode ?: UUIDGenerator.randomNumbersUUID()).let {
            currentProduct.value.product.barcode = it
            productBarcode.value = it
        }
    }

    fun updateProductImage(image: String) {
        currentProduct.value.product.image = image
        productImage.value = image
    }

    fun updateProductSuppliers(traders: List<Trader>) {
        currentProduct.value.traders = traders
    }

    fun updateProductCategories(categories: List<Category>) {
        currentProduct.value.categories = categories
    }

    fun updateProductBarcode(editable: Editable) {
        currentProduct.value.product.barcode = editable.toString()
    }

    fun updateProductName(editable: Editable) {
        currentProduct.value.product.name = editable.toString()
    }

    fun updateProductBuyPrice(editable: Editable) {
        currentProduct.value.product.buyPrice = editable.toString().toFloatOrNull() ?: 0F
    }

    fun updateProductSellPrice(editable: Editable) {
        currentProduct.value.product.sellPrice = editable.toString().toFloatOrNull() ?: 0F
    }

    fun updateProductSuggestedPrice(editable: Editable) {
        currentProduct.value.product.suggestedSellPrice = editable.toString().toFloatOrNull() ?: 0F
    }

    fun updateProductSku(editable: Editable) {
        currentProduct.value.product.sku = editable.toString()
    }

    fun updateProductBrand(editable: Editable) {
        currentProduct.value.product.brand = editable.toString()
    }

    fun updateProductSize(editable: Editable) {
        currentProduct.value.product.notes = editable.toString()
    }

    fun updateProductAmount(editable: Editable) {
        val newAmount = editable.toString().toIntOrNull() ?: 0
        currentProduct.value.product.apply {
            stock = newAmount
            historicInStock = newAmount
        }
    }
}

