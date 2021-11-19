package com.puntogris.blint.ui.product.categories

import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.map
import com.puntogris.blint.data.repository.categories.CategoriesRepository
import com.puntogris.blint.model.Category
import com.puntogris.blint.model.CheckableCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductCategoriesViewModel @Inject constructor(
    private val repository: CategoriesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val query = MutableLiveData("")

    private val initialCategories = savedStateHandle.get<Array<Category>>("categories") ?: emptyArray()

    private val initialCategoriesIds = initialCategories.map { it.categoryName }

    private val finalCategories = initialCategories.toMutableList()

    val categoriesLiveData = query.switchMap {
        repository.getCategoriesPaged(it).asLiveData()
    }.map {
        it.map { category ->
            CheckableCategory(
                category = category,
                isChecked = category.categoryName in initialCategoriesIds
            )
        }
    }.cachedIn(viewModelScope)

    fun setQuery(query: String) {
        this.query.value = query
    }

    fun toggleCategory(category: Category){
        if (category in finalCategories) finalCategories.remove(category)
        else finalCategories.add(category)
    }

    fun getFinalCategories() = finalCategories
}