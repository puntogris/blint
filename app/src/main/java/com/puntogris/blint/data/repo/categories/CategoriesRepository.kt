package com.puntogris.blint.data.repo.categories

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.puntogris.blint.data.local.dao.CategoriesDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.FirestoreQueries
import com.puntogris.blint.model.Category
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoriesRepository @Inject constructor(
    private val firestoreQueries: FirestoreQueries,
    private val usersDao: UsersDao,
    private val categoriesDao: CategoriesDao
): ICategoriesRepository {

    private suspend fun currentBusiness() = usersDao.getCurrentBusiness()

    override suspend fun deleteProductCategoryDatabase(categoryName: String): SimpleResult = withContext(
        Dispatchers.IO) {
        try {
            val user = currentBusiness()
            if (user.isBusinessOnline()) {
                firestoreQueries.getCategoriesCollectionQuery(user)
                    .document("categories")
                    .update("categories", FieldValue.arrayRemove(categoryName.lowercase()))
                    .await()
            }
            else categoriesDao.deleteCategory(categoryName)
            SimpleResult.Success
        } catch (e: Exception) {
            SimpleResult.Failure }
    }

    override suspend fun saveProductCategoryDatabase(category: Category): SimpleResult = withContext(
        Dispatchers.IO){
        try {
            val user = currentBusiness()
            val categoryRef = firestoreQueries.getCategoriesCollectionQuery(user).document("categories")

            if (user.isBusinessOnline()) {
                categoryRef.set(hashMapOf("categories" to FieldValue.arrayUnion(category.categoryName.lowercase())), SetOptions.merge()).await()
            }
            else {
                category.businessId = user.businessId
                categoriesDao.insert(category)
            }
            SimpleResult.Success
        }catch (e:Exception){
            SimpleResult.Failure }
    }

    override suspend fun getAllCategoriesDatabase(): RepoResult<List<Category>> = withContext(Dispatchers.IO){
        try {
            val user = currentBusiness()
            val data = if (user.isBusinessOnline()){
                val ref = firestoreQueries
                    .getCategoriesCollectionQuery(user).document("categories")

                val data = ref.get().await().get("categories") as List<*>
                data.map { Category(it.toString()) }
            }
            else{ categoriesDao.getAllCategories() }
            RepoResult.Success(data)
        }catch (e:Exception){
            RepoResult.Error(e)
        }
    }
}