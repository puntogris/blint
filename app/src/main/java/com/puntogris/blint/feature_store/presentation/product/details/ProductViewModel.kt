package com.puntogris.blint.feature_store.presentation.product.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails
import com.puntogris.blint.feature_store.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    handle: SavedStateHandle
) : ViewModel() {

    val currentProduct = handle.getStateFlow("productWithDetails", ProductWithDetails())

    @OptIn(ExperimentalCoroutinesApi::class)
    val productRecords = currentProduct.flatMapLatest {
        repository.getProductRecordsPaged(it.product.productId)
    }.cachedIn(viewModelScope)

    suspend fun deleteProductDatabase(): SimpleResource {
        return repository.deleteProduct(currentProduct.value.product.productId)
    }
}
