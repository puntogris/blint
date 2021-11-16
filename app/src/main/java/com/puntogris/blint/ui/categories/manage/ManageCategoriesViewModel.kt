package com.puntogris.blint.ui.categories.manage

import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.repository.categories.CategoriesRepository
import com.puntogris.blint.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@HiltViewModel
class ManageCategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
):ViewModel() {

    @ExperimentalCoroutinesApi
    suspend fun getProductCategories() = categoriesRepository.getAllCategoriesDatabase()

    suspend fun deleteCategory(name: String) =
        categoriesRepository.deleteProductCategoryDatabase(name)

    suspend fun saveCategoryDatabase(name: String) =
        categoriesRepository.saveProductCategoryDatabase(Category(categoryName = name.lowercase()))

}