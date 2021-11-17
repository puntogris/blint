package com.puntogris.blint.ui.product.manage

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.data.repository.products.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val query = MutableLiveData("")

    val productsLiveData = query.switchMap {
        if (it.isNullOrBlank()) {
            productRepository.getProductsWithSuppliersCategoriesPaged().asLiveData()
        } else {
            productRepository.getProductsSupplierCategoryWithQueryFlow(it).asLiveData()
        }
    }.cachedIn(viewModelScope)

    fun setQuery(query: String) {
        this.query.value = query
    }
}