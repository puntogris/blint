package com.puntogris.blint.ui.product.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.puntogris.blint.data.repo.products.ProductRepository
import com.puntogris.blint.model.ProductWithSuppliersCategories
import com.puntogris.blint.utils.SearchText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ManageProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository
): ViewModel() {

    suspend fun getProductsPaging() =
        productRepository.getProductsPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getProductWithName(search: SearchText) =
        productRepository.getProductsWithNamePagingDataFlow(search).cachedIn(viewModelScope)

}