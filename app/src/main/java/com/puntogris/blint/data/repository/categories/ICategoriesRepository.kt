package com.puntogris.blint.data.repository.categories

import androidx.paging.PagingData
import com.puntogris.blint.model.Category
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow

interface ICategoriesRepository {

    suspend fun deleteCategory(categoryName: String): SimpleResult

    suspend fun saveCategory(category: Category): SimpleResult

    fun getCategoriesPaged(query: String? = null): Flow<PagingData<Category>>
}