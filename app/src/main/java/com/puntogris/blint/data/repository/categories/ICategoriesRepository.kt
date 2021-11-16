package com.puntogris.blint.data.repository.categories

import com.puntogris.blint.model.Category
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult

interface ICategoriesRepository {
    suspend fun deleteProductCategoryDatabase(categoryName: String): SimpleResult
    suspend fun saveProductCategoryDatabase(category: Category): SimpleResult
    suspend fun getAllCategoriesDatabase(): RepoResult<List<Category>>
}