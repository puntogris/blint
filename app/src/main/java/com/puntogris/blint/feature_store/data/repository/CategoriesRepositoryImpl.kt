package com.puntogris.blint.feature_store.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.data.data_source.local.dao.CategoriesDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.UsersDao
import com.puntogris.blint.feature_store.domain.model.Category
import com.puntogris.blint.feature_store.domain.repository.CategoriesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CategoriesRepositoryImpl(
    private val usersDao: UsersDao,
    private val categoriesDao: CategoriesDao,
    private val dispatcher: DispatcherProvider
) : CategoriesRepository {

    override suspend fun deleteCategory(categoryName: String): SimpleResult =
        withContext(dispatcher.io) {
            try {
                categoriesDao.deleteCategory(categoryName)
                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override suspend fun saveCategory(category: Category): SimpleResult =
        withContext(dispatcher.io) {
            try {
                category.businessId = usersDao.getCurrentBusinessId()
                categoriesDao.insert(category)
                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override fun getCategoriesPaged(query: String?): Flow<PagingData<Category>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            if (query.isNullOrBlank()) categoriesDao.getCategoriesPaged()
            else categoriesDao.getCategoriesWithQueryPaged("%$query%")
        }.flow
    }
}