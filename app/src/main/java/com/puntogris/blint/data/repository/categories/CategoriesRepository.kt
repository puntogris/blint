package com.puntogris.blint.data.repository.categories

import com.puntogris.blint.data.data_source.local.dao.CategoriesDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.model.Category
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.RepoResult
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoriesRepository @Inject constructor(
    private val usersDao: UsersDao,
    private val categoriesDao: CategoriesDao,
    private val dispatcher: DispatcherProvider
) : ICategoriesRepository {

    private suspend fun currentBusiness() = usersDao.getCurrentBusinessFromUser()

    override suspend fun deleteProductCategoryDatabase(categoryName: String): SimpleResult =
        withContext(dispatcher.io) {
            try {
                categoriesDao.deleteCategory(categoryName)
                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override suspend fun saveProductCategoryDatabase(category: Category): SimpleResult =
        withContext(dispatcher.io) {
            try {
                val user = currentBusiness()
                category.businessId = user.businessId
                categoriesDao.insert(category)
                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override suspend fun getAllCategoriesDatabase(): RepoResult<List<Category>> =
        withContext(dispatcher.io) {
            try {
                val data = categoriesDao.getAllCategories()
                RepoResult.Success(data)
            } catch (e: Exception) {
                RepoResult.Error(e)
            }
        }
}