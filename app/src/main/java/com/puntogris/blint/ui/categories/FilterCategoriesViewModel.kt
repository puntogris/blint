package com.puntogris.blint.ui.categories

import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.repo.categories.CategoriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterCategoriesViewModel @Inject constructor(
    private val categoriesRepository: CategoriesRepository
): ViewModel() {

    suspend fun getAllCategories() = categoriesRepository.getAllCategoriesDatabase()
}