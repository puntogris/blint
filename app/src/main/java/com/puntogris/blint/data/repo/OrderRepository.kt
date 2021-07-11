package com.puntogris.blint.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.OrdersDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.FirestoreOrdersPagingSource
import com.puntogris.blint.data.remote.FirestoreQueries
import com.puntogris.blint.data.remote.FirestoreRecordsPagingSource
import com.puntogris.blint.data.repo.imp.IOrdersRepository
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val firestoreQueries: FirestoreQueries,
    private val usersDao: UsersDao,
    private val ordersDao: OrdersDao
):IOrdersRepository {

    private suspend fun currentBusiness() = usersDao.getUser()
    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    override suspend fun getBusinessOrdersPagingDataFlow(): Flow<PagingData<OrderWithRecords>> = withContext(
        Dispatchers.IO){
        val user = currentBusiness()
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200                )
        ) {
            if (user.currentBusinessIsOnline()){
                val query = firestoreQueries
                    .getOrdersCollectionQuery(user)
                    .whereEqualTo("businessId", user.currentBusinessId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                FirestoreOrdersPagingSource(query)
            }
            else{ ordersDao.getAllOrdersPaged() }
        }.flow
    }

    override suspend fun getBusinessRecordsPagingDataFlow(): Flow<PagingData<Record>> = withContext(Dispatchers.IO){
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
                    .whereEqualTo("businessId", user.currentBusinessId)
                FirestoreRecordsPagingSource(query)
            }
            else{ ordersDao.getAllRecordsPaged() }
        }.flow
    }

    override suspend fun saveOrderIntoDatabase(order: OrderWithRecords): SimpleResult = withContext(Dispatchers.IO){
        val user = currentBusiness()
        try {
            val orderRef = firestoreQueries.getOrdersCollectionQuery(user)
            order.order.author = auth.currentUser?.email.toString()
            order.order.businessId = user.currentBusinessId
            order.order.orderId = orderRef.document().id
            val recordsFinal = order.records.map {
                Record(
                    recordId = firestoreQueries.getRecordsCollectionQuery(user).document().id,
                    type = order.order.type,
                    traderName = order.order.traderName,
                    traderId = order.order.traderId,
                    timestamp = order.order.timestamp,
                    amount = it.amount,
                    productId = it.productId,
                    productName = it.productName,
                    author = order.order.author,
                    businessId = order.order.businessId,
                    productUnitPrice = it.value / it.amount,
                    value = it.value,
                    orderId = order.order.orderId
                )
            }

            if (user.currentBusinessIsOnline()) {
                val countersRef = firestoreQueries.getBusinessCountersQuery(user)
                countersRef.get().await().get("totalOrders").toString().toIntOrNull()?.let { order.order.number = it }
                firestore.runBatch { batch ->
                    batch.set(orderRef.document(order.order.orderId), FirestoreOrder.from(order))
                    recordsFinal.forEach {
                        val productRef = firestoreQueries.getProductsCollectionQuery(user).document(it.productId)
                        batch.update(productRef,"amount", FieldValue.increment(it.amount.toLong()))
                        batch.set(countersRef, hashMapOf("totalOrders" to FieldValue.increment(1)), SetOptions.merge())
                        batch.set(firestoreQueries.getRecordsCollectionQuery(user).document(it.recordId), it)
                    }
                }.await()
            }else{ ordersDao.insertOrderWithRecords(order, recordsFinal) }
            SimpleResult.Success
        }catch (e:Exception){ SimpleResult.Failure }
    }
}