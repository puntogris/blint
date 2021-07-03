package com.puntogris.blint.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.OrdersDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.FirestoreOrdersPagingSource
import com.puntogris.blint.data.remote.FirestoreQueries
import com.puntogris.blint.data.remote.FirestoreRecordsPagingSource
import com.puntogris.blint.data.repo.imp.IOrdersRepository
import com.puntogris.blint.model.Order
import com.puntogris.blint.model.Record
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

    override suspend fun getBusinessOrdersPagingDataFlow(): Flow<PagingData<Order>> = withContext(
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

    override suspend fun saveOrderIntoDatabase(order:Order): SimpleResult = withContext(Dispatchers.IO){
        val user = currentBusiness()
        try {
            val orderRef = firestoreQueries.getOrdersCollectionQuery(user)
            val recordRef = firestoreQueries.getRecordsCollectionQuery(user)
            order.updateOrderData(user.currentBusinessId, orderRef, recordRef)
            if (user.currentBusinessIsOnline()){
                firestore.runBatch { batch ->
                    batch.set(orderRef.document(order.orderId), order)
                    order.items.forEach {
                        val productRef = firestoreQueries.getProductsCollectionQuery(user).document(it.productId)
                        batch.update(productRef,"amount", FieldValue.increment(it.amount.toLong()))
                        batch.set(recordRef.document(it.recordId), it)
                    }
                }.await()
            }else{
                ordersDao.insertOrderWithRecords(order)
            }
            SimpleResult.Success
        }catch (e:Exception){ SimpleResult.Failure }
    }
}