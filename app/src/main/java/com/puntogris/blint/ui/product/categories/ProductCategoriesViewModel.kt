package com.puntogris.blint.ui.product.categories

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.data.repository.categories.CategoriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.toList
import javax.inject.Inject

@HiltViewModel
class ProductCategoriesViewModel @Inject constructor(
    private val repository: CategoriesRepository
) : ViewModel() {

    private val query = MutableLiveData("")

    val categoriesLiveData = query.switchMap {
        repository.getCategoriesPaged(it).asLiveData()
    }.cachedIn(viewModelScope)

    fun setQuery(query: String){
        this.query.value = query
    }
}