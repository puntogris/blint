package com.puntogris.blint.ui.categories

import androidx.lifecycle.ViewModel
import com.puntogris.blint.domain.repository.CategoriesRepository
import com.puntogris.blint.model.Category
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