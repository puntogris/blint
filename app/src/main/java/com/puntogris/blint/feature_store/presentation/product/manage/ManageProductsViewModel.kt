package com.puntogris.blint.feature_store.presentation.product.manage

import android.text.Editable
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.feature_store.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class ManageProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val query = MutableStateFlow("")

    @ExperimentalCoroutinesApi
    val productsFlow = query.flatMapLatest {
        if (it.isBlank()) {
            productRepository.getProductsPaged()
        } else {
            productRepository.getProductsWithQueryPaged(it)
        }
    }.cachedIn(viewModelScope)

    fun setQuery(query: String) {
        this.query.value = query
    }

    fun setQuery(editable: Editable) {
        query.value = editable.toString()
    }
}