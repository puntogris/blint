package com.puntogris.blint.data.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.OrdersDao
import com.puntogris.blint.data.local.dao.ProductsDao
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val usersDao: UsersDao,
    private val productsDao: ProductsDao,
    private val statisticsDao: StatisticsDao,
    private val ordersDao: OrdersDao
): IProductRepository {

    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private suspend fun currentBusiness() = usersDao.getUser()
    private fun getCurrentUid() = auth.currentUser

    override suspend fun saveProductDatabase(product: Product, suppliers:List<Int>, categories: List<Int>): SimpleResult = withContext(Dispatchers.IO){
        try {
            val business = currentBusiness()
            product.businessId = business.currentBusinessId
            if (business.currentBusinessType == "ONLINE"){
                val productRef =
                    firestore
                        .collection("users")
                        .document(business.currentBusinessOwner)
                        .collection("business")
                        .document(business.currentBusinessId)
                        .collection("products")
                        .document()

                val productCounterRef =
                    firestore
                        .collection("users")
                        .document(business.currentBusinessOwner)
                        .collection("business")
                        .document(business.currentBusinessId)

                firestore.runBatch {
                    it.set(productRef, product)
                    //If the product is not new, up the counter.
                    if (product.productId == 0)
                        it.update(productCounterRef,"products_counter", FieldValue.increment(1))

                    if (product.amount != 0){
                        val record = Record(
                            type = "IN",
                            amount = product.amount,
                            productId = product.productId,
                            productName = product.name,
                            timestamp = Timestamp.now(),
                            author = getCurrentUid()?.email.toString(),
                            businessId = business.currentBusinessId
                        )
                        val recordRef =
                            firestore
                                .collection("users")
                                .document(business.currentBusinessOwner)
                                .collection("business")
                                .document(business.currentBusinessId)
                                .collection("records")
                                .document()
                    it.set(recordRef, record)
                    }
                }.await()
            }else{
                productsDao.insert(product)
                if (product.productId == 0)
                    statisticsDao.incrementTotalProducts()

                //CrossRef data for suppliers and categories.
                suppliers.map {
                    ProductSupplierCrossRef(product.productId, it)
                }.let {
                    productsDao.insertProductSupplierCrossRef(it)
                }
                categories.map {
                    ProductCategoryCrossRef(product.productId, it)
                }.let {
                    productsDao.insertProductCategoriesCrossRef(it)
                }

                //Create initial stock record.
                if (product.amount != 0){
                    val record = Record(
                        type = "IN",
                        amount = product.amount,
                        productId = product.productId,
                        productName = product.name,
                        timestamp = Timestamp.now(),
                        author = getCurrentUid()?.email.toString(),
                        businessId = business.currentBusinessId
                    )
                    ordersDao.insert(record)
                }
            }
            SimpleResult.Success
        }catch (e:Exception){
            SimpleResult.Failure
        }
    }

    override suspend fun getProductsPagingDataFlow(): Flow<PagingData<Product>> = withContext(Dispatchers.IO) {
        val business = currentBusiness()
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200                )
        ) {
            if(business.currentBusinessType == "ONLINE"){
                val query = firestore
                    .collection("users")
                    .document(business.currentBusinessOwner)
                    .collection("business")
                    .document(business.currentBusinessId)
                    .collection("products")
                FirestoreProductsPagingSource(query)
            }
            else{
                productsDao.getAllPaged()
            }
        }.flow
    }

    override suspend fun deleteProductDatabase(productId: String): SimpleResult = withContext(Dispatchers.IO){
        try {
            val business = currentBusiness()
            if (business.typeIsOnline()){
                firestore
                    .collection("users")
                    .document(business.currentBusinessOwner)
                    .collection("business")
                    .document(business.currentBusinessId)
                    .collection("products")
                    .document(productId)
                    .delete()
                    .await()
            }else{
                productsDao.delete(productId.toInt())
                statisticsDao.decrementTotalProducts()
            }
            SimpleResult.Success
        }catch (e:Exception){
            SimpleResult.Failure
        }
    }

}