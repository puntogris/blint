package com.puntogris.blint.feature_store.presentation.product.categories

import android.text.Editable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.puntogris.blint.feature_store.domain.model.Category
import com.puntogris.blint.feature_store.domain.model.CheckableCategory
import com.puntogris.blint.feature_store.domain.repository.CategoriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ProductCategoriesViewModel @Inject constructor(
    private val repository: CategoriesRepository,
    handle: SavedStateHandle
) : ViewModel() {

    private val query = MutableStateFlow("")

    private val initialCategories =
        ProductCategoryFragmentArgs.fromSavedStateHandle(handle).categories ?: emptyArray()

    private val initialCategoriesIds = initialCategories.map { it.categoryName }

    private val finalCategories = initialCategories.toMutableList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val categoriesFlow = query.flatMapLatest {
        repository.getCategoriesPaged(it)
    }.map {
        it.map { category ->
            CheckableCategory(
                category = category,
                isChecked = category.categoryName in initialCategoriesIds
            ).also {
                println(it)
            }
        }
    }.cachedIn(viewModelScope)

    fun setQuery(editable: Editable) {
        query.value = editable.toString()
    }

    fun toggleCategory(category: Category) {
        if (category in finalCategories) finalCategories.remove(category)
        else finalCategories.add(category)
    }

    fun getFinalCategories() = finalCategories
}