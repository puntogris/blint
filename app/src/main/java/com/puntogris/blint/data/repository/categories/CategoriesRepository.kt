package com.puntogris.blint.data.repository.categories

import com.puntogris.blint.data.data_source.local.dao.CategoriesDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.data.data_source.remote.FirestoreQueries
import com.puntogris.blint.model.Category
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoriesRepository @Inject constructor(
    private val usersDao: UsersDao,
    private val categoriesDao: CategoriesDao
) : ICategoriesRepository {

    private suspend fun currentBusiness() = usersDao.getCurrentBusinessFromUser()

    override suspend fun deleteProductCategoryDatabase(categoryName: String): SimpleResult =
        withContext(
            Dispatchers.IO
        ) {
            try {
                categoriesDao.deleteCategory(categoryName)
                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override suspend fun saveProductCategoryDatabase(category: Category): SimpleResult =
        withContext(
            Dispatchers.IO
        ) {
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
        withContext(Dispatchers.IO) {
            try {
                val data = categoriesDao.getAllCategories()
                RepoResult.Success(data)
            } catch (e: Exception) {
                RepoResult.Error(e)
            }
        }
}