package com.puntogris.blint.data.repo.products

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.paging.*
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.AppDatabase
import com.puntogris.blint.data.local.dao.*
import com.puntogris.blint.data.remote.FirestoreProductsPagingSource
import com.puntogris.blint.data.remote.FirestoreQueries
import com.puntogris.blint.data.remote.FirestoreRecordsPagingSource
import com.puntogris.blint.data.remote.deserializers.ProductDeserializer
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.Constants.INITIAL
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SearchText
import com.puntogris.blint.utils.SimpleResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val usersDao: UsersDao,
    private val productsDao: ProductsDao,
    private val statisticsDao: StatisticsDao,
    private val ordersDao: OrdersDao,
    private val firestoreQueries: FirestoreQueries,
    private val suppliersDao: SuppliersDao,
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
            val recordRef = firestoreQueries.getRecordsCollectionQuery(user)

            if (isNewProduct){
                product.product.apply {
                    productId = productRef.document().id
                    businessId = user.currentBusinessId
                    totalInStock = amount
                }
            }

            val record = Record(
                type = INITIAL,
                amount = product.product.amount,
                productId = product.product.productId,
                productName = product.product.name,
                timestamp = Timestamp.now(),
                author = getCurrentUid()?.email.toString(),
                businessId = user.currentBusinessId,
                barcode = product.product.barcode,
                totalInStock = product.product.amount,
                sku = product.product.sku,
                recordId = recordRef.document().id
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
                    if (isNewProduct){
                        it.set(productCounterRef, hashMapOf("totalProducts" to FieldValue.increment(1)), SetOptions.merge())
                        if (product.product.amount != 0) it.set(recordRef.document(record.recordId), record)
                    }
                }.await()
            }else{

                productsDao.insertProduct(product)
                if (isNewProduct) statisticsDao.incrementTotalProducts()
                if (product.product.amount != 0) ordersDao.insert(record)
            }
            SimpleResult.Success
        }catch (e:Exception){
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
                val query = firestoreQueries.getProductsCollectionQuery(user).orderBy("name", Query.Direction.ASCENDING)
                FirestoreProductsPagingSource(query)
            }
            else{ productsDao.getAllPaged() }
        }.flow
    }

    override suspend fun deleteProductDatabase(productId: String): SimpleResult = withContext(Dispatchers.IO){
        try {
            val user = currentBusiness()
            if (user.currentBusinessIsOnline()){

                firestore.runBatch {
                    it.delete(firestoreQueries.getProductsCollectionQuery(user).document(productId))
                    it.update(firestoreQueries.getBusinessCountersQuery(user),"totalProducts", FieldValue.increment(-1))
                }.await()

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
        if(!user.currentBusinessIsOnline() && search is SearchText.Category){
            val ids = productsDao.getProductIdWithCategory(search.text)
            Pager(
                PagingConfig(
                    pageSize = 30,
                    enablePlaceholders = true,
                    maxSize = 200
                )
            ){ productsDao.getPagedProductsWithCategory(ids) }.flow
        }else {
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
                                    .whereEqualTo("sku", search.text.uppercase())
                            }
                            is SearchText.Name -> {
                                firestoreQueries
                                    .getProductsCollectionQuery(user)
                                    .whereArrayContains("search_name", search.text.lowercase())
                            }
                            is SearchText.QrCode -> {
                                firestoreQueries
                                    .getProductsCollectionQuery(user)
                                    .whereEqualTo("barcode", search.text)
                            }
                            is SearchText.Category -> {
                                firestoreQueries
                                    .getProductsCollectionQuery(user)
                                    .whereArrayContains("categories", search.text.lowercase())
                            }
                        }
                    FirestoreProductsPagingSource(query)
                }
                else productsDao.getPagedSearch("%${search.getData()}%")
            }.flow
        }
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

    override suspend fun getSuppliersWithNameDatabase(search: String): List<FirestoreSupplier>  = withContext(Dispatchers.IO){
        val user = currentBusiness()
        if (user.currentBusinessIsOnline()){
            firestoreQueries
                .getSuppliersCollectionQuery(user)
                .whereArrayContains("search_name", search).limit(5).get().await().toObjects(FirestoreSupplier::class.java)
        }
        else{ suppliersDao.getProductSupplier("%${search}%") }
    }

    override suspend fun getProductWithBarcode(barcode: String): RepoResult<ProductWithSuppliersCategories> = withContext(Dispatchers.IO){
        try {
            val user = currentBusiness()
            val product:ProductWithSuppliersCategories? =
                if (user.currentBusinessIsOnline()){
                    val data = firestoreQueries
                        .getProductsCollectionQuery(user)
                        .whereEqualTo("barcode", barcode).limit(1).get().await()
                    val prod = ProductDeserializer.deserialize(data.first())
                    if (prod.product.productId.isNotEmpty() && prod.product.barcode.isNotEmpty())
                        prod
                    else null
                }
                else{ productsDao.getProductWithBarcode(barcode)}

            if (product != null) RepoResult.Success(product)
            else RepoResult.Error(Exception())
        }catch (e:Exception){
            RepoResult.Error(e)
        }
    }
}