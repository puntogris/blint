package com.puntogris.blint.feature_store.domain.repository

import androidx.paging.PagingData
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {

    suspend fun deleteCategory(categoryName: String): SimpleResult

    suspend fun saveCategory(category: Category): SimpleResult

    fun getCategoriesPaged(query: String? = null): Flow<PagingData<Category>>
}