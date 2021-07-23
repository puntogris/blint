package com.puntogris.blint.data.repo.categories

import com.puntogris.blint.model.Category
import com.puntogris.blint.model.FirestoreCategory
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.Flow

interface ICategoriesRepository {
    suspend fun deleteProductCategoryDatabase(categories: List<Category>): SimpleResult
    suspend fun saveProductCategoryDatabase(category: Category): SimpleResult
    suspend fun getProductCategoriesDatabase(): Flow<List<Category>>
    suspend fun updateProductCategoryDatabase(category: Category): SimpleResult
    suspend fun getCategoriesWithNameDatabase(search: String): List<FirestoreCategory>
}