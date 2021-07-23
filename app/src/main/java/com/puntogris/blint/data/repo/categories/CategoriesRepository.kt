package com.puntogris.blint.data.repo.categories

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.CategoriesDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.FirestoreQueries
import com.puntogris.blint.model.Category
import com.puntogris.blint.model.FirestoreCategory
import com.puntogris.blint.model.FirestoreSearchCategory
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoriesRepository @Inject constructor(
    private val firestoreQueries: FirestoreQueries,
    private val usersDao: UsersDao,
    private val categoriesDao: CategoriesDao
): ICategoriesRepository {

    private suspend fun currentBusiness() = usersDao.getUser()
    private val firestore = Firebase.firestore


    override suspend fun deleteProductCategoryDatabase(categories: List<Category>): SimpleResult = withContext(
        Dispatchers.IO) {
        try {
            val user = currentBusiness()
            if (user.currentBusinessIsOnline()) {
                val categoryRef = firestoreQueries.getCategoriesCollectionQuery(user)

                firestore.runBatch { batch ->
                    categories.forEach {
                        batch.delete(categoryRef.document(it.categoryId))
                    }
                }.await()
            }
            else categoriesDao.deleteCategory(categories)
            SimpleResult.Success
        } catch (e: Exception) {
            SimpleResult.Failure }
    }

    override suspend fun saveProductCategoryDatabase(category: Category): SimpleResult = withContext(
        Dispatchers.IO){
        try {
            val user = currentBusiness()
            val categoryRef = firestoreQueries.getCategoriesCollectionQuery(user).document()

            if (user.currentBusinessIsOnline()) {

                val searchList = mutableListOf<String>()
                category.name.forEachIndexed { index, c ->
                    if(!c.isWhitespace()){
                        for (i in 1..category.name.length - index){
                            searchList.add(category.name.substring(index, index + i))
                        }
                    }
                }
                val final = FirestoreSearchCategory(
                    businessId = user.currentBusinessId,
                    categoryId = categoryRef.id,
                    name = category.name,
                    search_name = searchList.distinct()
                )
                categoryRef.set(final).await()
            }
            else {
                category.apply {
                    businessId = user.currentBusinessId
                    categoryId = categoryRef.id
                }
                categoriesDao.insert(category)
            }
            SimpleResult.Success
        }catch (e:Exception){ SimpleResult.Failure }
    }

    @ExperimentalCoroutinesApi
    override suspend fun getProductCategoriesDatabase(): Flow<List<Category>> = withContext(
        Dispatchers.IO){
        val user = currentBusiness()
        return@withContext if(user.currentBusinessIsOnline()) {
            callbackFlow {
                val ref = firestoreQueries.getCategoriesCollectionQuery(user)
                    .orderBy("name", Query.Direction.ASCENDING)
                    .addSnapshotListener { snapshot, _ ->
                        if (snapshot != null) {
                            val data = snapshot.toObjects(Category::class.java)
                            this.trySend(data)
                        }
                    }
                awaitClose { ref.remove() }
            }
        }else{ categoriesDao.getAllCategoriesFlow() }
    }

    override suspend fun updateProductCategoryDatabase(category: Category): SimpleResult = withContext(
        Dispatchers.IO){
        try {
            val user = currentBusiness()
            if (user.currentBusinessIsOnline()) {
                firestoreQueries.getCategoriesCollectionQuery(user)
                    .document(category.categoryId)
                    .update("name", category.name).await()
            }
            else categoriesDao.update(category)
            SimpleResult.Success
        }catch (e:Exception){ SimpleResult.Failure }
    }

    override suspend fun getCategoriesWithNameDatabase(search: String) = withContext(Dispatchers.IO){
        val user = currentBusiness()
        if (user.currentBusinessIsOnline()){
            firestoreQueries
                .getCategoriesCollectionQuery(user)
                .whereArrayContains("search_name", search).limit(5).get().await().toObjects(
                    FirestoreCategory::class.java)
        }
        else{ categoriesDao.getCategoriesWithName("%${search}%") }
    }
}