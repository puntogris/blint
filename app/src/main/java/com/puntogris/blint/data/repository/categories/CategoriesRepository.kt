package com.puntogris.blint.data.repository.categories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.data_source.local.dao.CategoriesDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.model.Category
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoriesRepository @Inject constructor(
    private val usersDao: UsersDao,
    private val categoriesDao: CategoriesDao,
    private val dispatcher: DispatcherProvider
) : ICategoriesRepository {

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

    override fun getCategoriesPaged(): Flow<PagingData<Category>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) { categoriesDao.getCategoriesPaged() }.flow
    }
}