package com.puntogris.blint.ui.categories

import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.repo.categories.CategoriesRepository
import com.puntogris.blint.model.Category
import com.puntogris.blint.utils.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
):ViewModel() {

    @ExperimentalCoroutinesApi
    suspend fun getProductCategories() = categoriesRepository.getProductCategoriesDatabase()

    suspend fun deleteCategory(categories: List<Category>) =
        categoriesRepository.deleteProductCategoryDatabase(categories)

    suspend fun saveCategoryDatabase(name: String) =
        categoriesRepository.saveProductCategoryDatabase(Category(name = name.lowercase()))

    suspend fun updateCategoryDatabase(category: Category): SimpleResult {
        category.name = category.name.lowercase()
        return categoriesRepository.updateProductCategoryDatabase(category)
    }

    suspend fun getCategoriesWithName(name:String) =
        categoriesRepository.getCategoriesWithNameDatabase(name.lowercase())

}