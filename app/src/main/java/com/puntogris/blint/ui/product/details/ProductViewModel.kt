package com.puntogris.blint.ui.product.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.data.repository.products.ProductRepository
import com.puntogris.blint.model.product.ProductWithDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productWithDetails = savedStateHandle.get<ProductWithDetails>("productWithDetails")!!

    private val _currentProduct = MutableStateFlow(productWithDetails)
    val currentProduct = _currentProduct.asStateFlow()

    val productRecords = repository.getProductRecordsPaged(productWithDetails.product.productId)
        .cachedIn(viewModelScope)

    suspend fun deleteProductDatabase(productId: String) = repository.deleteProduct(productId)
}