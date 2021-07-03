package com.puntogris.blint.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.OrdersDao
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.SuppliersDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.FirestoreQueries
import com.puntogris.blint.data.remote.FirestoreRecordsPagingSource
import com.puntogris.blint.data.remote.FirestoreSuppliersPagingSource
import com.puntogris.blint.data.repo.imp.ISupplierRepository
import com.puntogris.blint.model.Record
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SupplierRepository @Inject constructor(
    private val suppliersDao: SuppliersDao,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao,
    private val ordersDao: OrdersDao,
    private val firestoreQueries: FirestoreQueries
): ISupplierRepository {

    private val firestore = Firebase.firestore

    private suspend fun currentBusiness() = usersDao.getUser()

    override suspend fun saveSupplierDatabase(supplier: Supplier): SimpleResult = withContext(Dispatchers.IO){
        try {
            val user = currentBusiness()
            val supplierRef = firestoreQueries.getSuppliersCollectionQuery(user).document()
            supplier.apply {
                businessId = user.currentBusinessId
                supplierId = supplierRef.id
            }
            if (user.currentBusinessIsOnline()){
                val supplierRefCounter = firestoreQueries.getBusinessCollectionQuery(user)
                firestore.runBatch {
                    it.set(supplierRef, supplier)
                    if (supplier.supplierId == "")
                        it.update(supplierRefCounter, "suppliers_counter", FieldValue.increment(1))
                }.await()
            }else{
                suppliersDao.insert(supplier)
                if (supplier.supplierId == "") statisticsDao.incrementTotalSuppliers()
            }
            SimpleResult.Success
        }catch (e:Exception){ SimpleResult.Failure }
    }

    override suspend fun getSupplierPagingDataFlow(): Flow<PagingData<Supplier>> = withContext(Dispatchers.IO){
        val user = currentBusiness()
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200                )
        ) {
            if(user.currentBusinessIsOnline()){
                val query = firestoreQueries.getSuppliersCollectionQuery(user)
                FirestoreSuppliersPagingSource(query)
            }
            else{ suppliersDao.getAllPaged() }
        }.flow
    }

    override suspend fun deleteSupplierDatabase(supplierId: String): SimpleResult = withContext(Dispatchers.IO) {
        try {
            val user = currentBusiness()
            if (user.currentBusinessIsOnline()){
                firestoreQueries.getSuppliersCollectionQuery(user)
                    .document(supplierId)
                    .delete()
                    .await()
            }else{
                suppliersDao.delete(supplierId)
                statisticsDao.decrementTotalSuppliers()
            }
            SimpleResult.Success
        }catch (e:Exception){ SimpleResult.Failure } }

    override suspend fun getSupplierRecordsPagingDataFlow(supplierId: String): Flow<PagingData<Record>> = withContext(Dispatchers.IO) {
        val user = currentBusiness()
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200                )
        ) {
            if (user.currentBusinessIsOnline()){
                val query = firestoreQueries.getRecordsWithTraderIdQuery(user, supplierId)
                FirestoreRecordsPagingSource(query)
            }
            else{ ordersDao.getSupplierRecords(supplierId) }
        }.flow
    }

    override suspend fun getSupplierWithNamePagingDataFlow(name: String): Flow<PagingData<Supplier>> = withContext(Dispatchers.IO){
        val user = currentBusiness()
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200                )
        ) {
            if (user.currentBusinessIsOnline()){
                val query = firestoreQueries
                    .getSuppliersCollectionQuery(user)
                    .whereArrayContains("search_name", name)
                    .limit(5)

                FirestoreSuppliersPagingSource(query)
            }
            else{ suppliersDao.getPagedSearch("%${name}%") }
        }.flow
    }

}