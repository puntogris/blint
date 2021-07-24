package com.puntogris.blint.ui.categories

import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.repo.categories.CategoriesRepository
import com.puntogris.blint.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterCategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
): ViewModel() {

    var categories = listOf<Category>()

    suspend fun getAllCategories() = categoriesRepository.getAllCategoriesDatabase()
}