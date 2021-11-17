package com.puntogris.blint.ui.product.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.data.repository.products.ProductRepository
import com.puntogris.blint.utils.SearchText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    suspend fun getProductsPaging() =
        productRepository.getProductsPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getProductWithName(search: SearchText) =
        productRepository.getProductsWithNamePagingDataFlow(search).cachedIn(viewModelScope)

}