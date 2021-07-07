package com.puntogris.blint.data.repo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.*
import com.puntogris.blint.data.local.dao.*
import com.puntogris.blint.data.remote.FirestoreProductsPagingSource
import com.puntogris.blint.data.remote.FirestoreQueries
import com.puntogris.blint.data.remote.FirestoreRecordsPagingSource
import com.puntogris.blint.data.repo.imp.IProductRepository
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.Constants.IN
import com.puntogris.blint.utils.SearchText
import com.puntogris.blint.utils.SimpleResult
import dagger.hilt.android.qualifiers.ApplicationContext
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val usersDao: UsersDao,
    private val productsDao: ProductsDao,
    private val statisticsDao: StatisticsDao,
    private val ordersDao: OrdersDao,
    private val firestoreQueries: FirestoreQueries,
    private val categoriesDao: CategoriesDao,
    @ApplicationContext private val context: Context
): IProductRepository {

    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private suspend fun currentBusiness() = usersDao.getUser()
    private fun getCurrentUid() = auth.currentUser

    override suspend fun saveProductDatabase(product: ProductWithSuppliersCategories, imageChanged: Boolean): SimpleResult = withContext(Dispatchers.IO){
        try {
            val isNewProduct = product.product.productId.isEmpty()
            val user = currentBusiness()
            val productRef = firestoreQueries.getProductsCollectionQuery(user)
            if (isNewProduct){
                product.product.apply {
                    productId = productRef.document().id
                    businessId = user.currentBusinessId
                }
            }

            val record = Record(
                type = IN,
                amount = product.product.amount,
                productId = product.product.productId,
                productName = product.product.name,
                timestamp = Timestamp.now(),
                author = getCurrentUid()?.email.toString(),
                businessId = user.currentBusinessId
            )

            if (user.currentBusinessIsOnline()){
                if (imageChanged){
                    product.product.image =
                    if (product.product.image.isNotEmpty()){
                        val imageCompressedName = "${user.currentBusinessId}_${product.product.productId}"
//                        val compressedImageFile = Compressor.compress(context, File(imagePath)) {
//                            resolution(544, 306)
//                            quality(50)
//                            format(Bitmap.CompressFormat.JPEG)
//                            size(80_000)
//                        }
                        val uri: Uri = Uri.parse(product.product.image)
                        val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
                        val data = baos.toByteArray()
                        bitmap.recycle()

                        val storageRef = firestoreQueries.getUserBusinessProductImagesQuery(user, imageCompressedName)
                        val task = storageRef.putBytes(data).await().task
                        task.continueWithTask { uriTask ->
                            if (!uriTask.isSuccessful){
                                uriTask.exception?.let {
                                    throw it
                                }
                            }
                            storageRef.downloadUrl
                        }.await().toString()
                    }else ""
                }

                val productCounterRef = firestoreQueries.getBusinessCountersQuery(user)

                firestore.runBatch {
                    it.set(productRef.document(product.product.productId), FirestoreProduct.from(product))
                    if (isNewProduct)
                        it.set(productCounterRef, hashMapOf("totalProducts" to FieldValue.increment(1)), SetOptions.merge())
                    if (product.product.amount != 0){
                        val recordRef = firestoreQueries.getRecordsCollectionQuery(user).document()
                        it.set(recordRef, record)
                    }
                }.await()
            }else{
                productsDao.insertProduct(product)
                if (isNewProduct) statisticsDao.incrementTotalProducts()
                if (product.product.amount != 0) ordersDao.insert(record)
            }
            SimpleResult.Success
        }catch (e:Exception){
            println(e.localizedMessage)
            SimpleResult.Failure
        }
    }

    override suspend fun getProductsPagingDataFlow(): Flow<PagingData<ProductWithSuppliersCategories>> = withContext(Dispatchers.IO) {
        val user = currentBusiness()
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200                )
        ) {
            if(user.currentBusinessIsOnline()){
                val query = firestoreQueries.getProductsCollectionQuery(user)
                FirestoreProductsPagingSource(query)
            }
            else{ productsDao.getAllPaged() }
        }.flow
    }

    override suspend fun deleteProductDatabase(productId: String): SimpleResult = withContext(Dispatchers.IO){
        try {
            val user = currentBusiness()
            if (user.currentBusinessIsOnline()){
                firestoreQueries.getProductsCollectionQuery(user)
                    .document(productId)
                    .delete()
                    .await()
            }else{
                productsDao.delete(productId)
                statisticsDao.decrementTotalProducts()
            }
            SimpleResult.Success
        }catch (e:Exception){
            SimpleResult.Failure
        }
    }

    override suspend fun getProductsWithNamePagingDataFlow(search: SearchText) = withContext(Dispatchers.IO){
        val user = currentBusiness()
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            if (user.currentBusinessIsOnline()){
                val query =
                    when(search){
                        is SearchText.InternalCode -> {
                            firestoreQueries
                                .getProductsCollectionQuery(user)
                                .whereEqualTo("internalCode", search.text)
                        }
                        is SearchText.Name -> {
                            firestoreQueries
                                .getProductsCollectionQuery(user)
                                .whereArrayContains("search_name", search.text)
                        }
                        is SearchText.QrCode -> {
                            firestoreQueries
                                .getProductsCollectionQuery(user)
                                .whereEqualTo("barcode", search.text)
                        }
                    }
                FirestoreProductsPagingSource(query)
            }
            else{ productsDao.getPagedSearch("%${search.getData()}%") }
        }.flow
    }

    override suspend fun getProductRecordsPagingDataFlow(productId: String) = withContext(Dispatchers.IO){
        val user = currentBusiness()
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200                )
        ) {
            if (user.currentBusinessIsOnline()){
                val query = firestoreQueries
                    .getRecordsCollectionQuery(user)
                    .whereEqualTo("productId", productId)
                FirestoreRecordsPagingSource(query)
            }
            else{ ordersDao.getProductRecordsPaged(productId) }
        }.flow
    }

    override suspend fun deleteProductCategoryDatabase(categories: List<Category>): SimpleResult = withContext(Dispatchers.IO) {
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

    override suspend fun saveProductCategoryDatabase(category: Category): SimpleResult = withContext(Dispatchers.IO){
        try {
            val user = currentBusiness()
            val categoryRef = firestoreQueries.getCategoriesCollectionQuery(user).document()
            category.apply {
                businessId = user.currentBusinessId
                categoryId = categoryRef.id
            }
            if (user.currentBusinessIsOnline()) categoryRef.set(category)
            else categoriesDao.insert(category)
            SimpleResult.Success
        }catch (e:Exception){ SimpleResult.Failure }
    }

    @ExperimentalCoroutinesApi
    override suspend fun getProductCategoriesDatabase(): Flow<List<Category>> = withContext(Dispatchers.IO){
        val user = currentBusiness()
        return@withContext if(user.currentBusinessIsOnline()) {
            callbackFlow {
                val ref = firestoreQueries.getCategoriesCollectionQuery(user)
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

    override suspend fun updateProductCategoryDatabase(category: Category): SimpleResult = withContext(Dispatchers.IO){
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
}