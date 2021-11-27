package com.puntogris.blint.feature_store.presentation.product.manage

import android.text.Editable
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.feature_store.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val query = MutableLiveData("")

    val productsLiveData = query.switchMap {
        if (it.isNullOrBlank()) {
            productRepository.getProductsPaged().asLiveData()
        } else {
            productRepository.getProductsWithQueryPaged(it).asLiveData()
        }
    }.cachedIn(viewModelScope)

    fun setQuery(query: String) {
        this.query.value = query
    }

    fun setQuery(editable: Editable) {
        query.value = editable.toString()
    }
}