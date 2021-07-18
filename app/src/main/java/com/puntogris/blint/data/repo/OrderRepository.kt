 package com.puntogris.blint.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.auth.FirebaseAuth
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
import com.puntogris.blint.data.remote.deserializers.OrderDeserializer
import com.puntogris.blint.data.repo.irepo.IOrdersRepository
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.Constants.CLIENT
import com.puntogris.blint.utils.Constants.IN
import com.puntogris.blint.utils.Constants.SUPPLIER
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.absoluteValue

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
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                FirestoreRecordsPagingSource(query)
            }
            else{ ordersDao.getAllRecordsPaged() }
        }.flow
    }

    override suspend fun saveOrderIntoDatabase(order: OrderWithRecords): SimpleResult = withContext(Dispatchers.IO){
        val user = currentBusiness()
        try {
            val orderRef = firestoreQueries.getOrdersCollectionQuery(user)

            //entra una orden pero necesito separarlo para room y juntarlo para firestore

            order.order.author = auth.currentUser?.email.toString()
            order.order.businessId = user.currentBusinessId
            order.order.orderId = orderRef.document().id
            order.order.businessName = user.currentBusinessName
            val debtRef = firestoreQueries.getDebtCollectionQuery(user)

            if (order.debt != null){
                order.order.debtId = debtRef.document().id
                order.debt?.debtId = order.order.debtId
                order.debt?.type = if (order.order.type == IN) SUPPLIER else CLIENT
            }

            val recordsFinal = order.records.map {
                val id = firestoreQueries.getRecordsCollectionQuery(user).document().id
                it.recordId = id
                Record(
                    recordId = id,
                    type = order.order.type,
                    traderName = order.order.traderName,
                    traderId = order.order.traderId,
                    timestamp = order.order.timestamp,
                    amount = it.amount,
                    productId = it.productId,
                    productName = it.productName,
                    author = order.order.author,
                    businessId = order.order.businessId,
                    productUnitPrice = if(it.amount != 0) it.value / it.amount else 0f,
                    value = it.value,
                    orderId = order.order.orderId,
                    totalInStock = it.totalInStock,
                    totalOutStock = it.totalOutStock,
                    sku = it.sku,
                    barcode = it.barcode
                ).also { rec ->
                    if (rec.type == IN) rec.totalInStock += rec.amount.absoluteValue
                    else rec.totalOutStock += rec.amount.absoluteValue
                }
            }
            if (user.currentBusinessIsOnline()) {
                val countersRef = firestoreQueries.getBusinessCountersQuery(user)
                countersRef.get().await().get("totalOrders").toString().toIntOrNull()?.let { order.order.number = it }

                firestore.runBatch { batch ->
                    if (order.debt != null){
                        val debt = Debt(
                            orderId = order.order.orderId,
                            debtId = order.debt!!.debtId,
                            amount = order.debt!!.amount,
                            traderId = order.order.traderId,
                            traderName = order.order.traderName,
                            author = order.order.author,
                            businessId = order.order.businessId,
                            type = order.debt!!.type
                        )
                        batch.set(debtRef.document(debt.debtId), debt)
                        val updateCounterRef = firestoreQueries.getBusinessCountersQuery(user)
                        if (order.debt?.type == "CLIENT"){
                            val updateClientRef = firestoreQueries.getClientsCollectionQuery(user)
                            batch.update(updateClientRef.document(debt.traderId), "debt",FieldValue.increment(debt.amount.toLong()))
                            batch.update(updateCounterRef,"clientsDebt", FieldValue.increment(debt.amount.toLong()))
                        }else{
                            val updateSupplierRef = firestoreQueries.getSuppliersCollectionQuery(user)
                            batch.update(updateSupplierRef.document(debt.traderId), "debt",FieldValue.increment(debt.amount.toLong()))
                            batch.update(updateCounterRef,"suppliersDebt", FieldValue.increment(debt.amount.toLong()))
                        }
                    }
                    batch.set(orderRef.document(order.order.orderId), FirestoreOrder.from(order))
                    recordsFinal.forEach {
                        val productRef = firestoreQueries.getProductsCollectionQuery(user).document(it.productId)
                        batch.update(
                            productRef,
                            "amount", FieldValue.increment(it.amount.toLong()),
                            "totalInStock", it.totalInStock.absoluteValue,
                            "totalOutStock", it.totalOutStock.absoluteValue
                        )
                        batch.set(countersRef, hashMapOf("totalOrders" to FieldValue.increment(1)), SetOptions.merge())
                        batch.set(firestoreQueries.getRecordsCollectionQuery(user).document(it.recordId), it)
                    }
                }.await()
            }else{
                ordersDao.insertOrderWithRecords(order, recordsFinal)
            }
            SimpleResult.Success
        }catch (e:Exception){ SimpleResult.Failure }
    }

     fun test(){

     }

    override suspend fun getOrderRecords(orderId: String): OrderWithRecords = withContext(Dispatchers.IO){
        val user = currentBusiness()
        if (user.currentBusinessIsOnline()){
            val query =
                firestoreQueries.getOrdersCollectionQuery(user)
                    .whereEqualTo("orderId", orderId).limit(1).get().await()
            OrderDeserializer.deserialize(query.first())
        }else{
            ordersDao.getAllOrderRecords(orderId)
        }
    }

}