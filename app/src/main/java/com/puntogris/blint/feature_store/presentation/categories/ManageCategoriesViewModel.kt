package com.puntogris.blint.feature_store.presentation.categories

import androidx.lifecycle.ViewModel
import com.puntogris.blint.feature_store.domain.model.Category
import com.puntogris.blint.feature_store.domain.repository.CategoriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageCategoriesViewModel @Inject constructor(
    private val repository: CategoriesRepository
) : ViewModel() {

    fun getProductCategories() = repository.getCategoriesPaged()

    suspend fun deleteCategory(name: String) = repository.deleteCategory(name)

    suspend fun saveCategory(name: String) =
        repository.saveCategory(Category(categoryName = name.lowercase()))
}
