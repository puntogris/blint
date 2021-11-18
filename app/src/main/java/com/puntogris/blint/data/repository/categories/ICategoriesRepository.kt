package com.puntogris.blint.data.repository.categories

import com.puntogris.blint.model.Category
import com.puntogris.blint.utils.types.RepoResult
import com.puntogris.blint.utils.types.SimpleResult

interface ICategoriesRepository {

    suspend fun deleteProductCategoryDatabase(categoryName: String): SimpleResult

    suspend fun saveProductCategoryDatabase(category: Category): SimpleResult

    suspend fun getAllCategoriesDatabase(): RepoResult<List<Category>>
}