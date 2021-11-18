package com.puntogris.blint.data.repository.categories

import com.puntogris.blint.model.Category
import com.puntogris.blint.utils.types.RepoResult
import com.puntogris.blint.utils.types.SimpleResult

interface ICategoriesRepository {

    suspend fun deleteCategory(categoryName: String): SimpleResult

    suspend fun saveCategory(category: Category): SimpleResult

    suspend fun getCategories(): RepoResult<List<Category>>
}