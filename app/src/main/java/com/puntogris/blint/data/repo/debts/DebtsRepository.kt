package com.puntogris.blint.data.repo.debts

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.DebtsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.FirestoreClientsPagingSource
import com.puntogris.blint.data.remote.FirestoreDebtsPagingSource
import com.puntogris.blint.data.remote.FirestoreQueries
import com.puntogris.blint.data.remote.FirestoreSuppliersPagingSource
import com.puntogris.blint.model.BusinessDebtsData
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Debt
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class DebtsRepository @Inject constructor(
    private val debtsDao: DebtsDao,
    private val usersDao: UsersDao,
    private val firestoreQueries: FirestoreQueries
): IDebtsRepository {

    private suspend fun currentUser() = usersDao.getCurrentBusinessFromUser()

    private val firestore = Firebase.firestore

    override suspend fun getLastTraderDebts(traderId: String): RepoResult<List<Debt>> {
        val user = currentUser()
        return try {
            val data =
                if (user.isBusinessOnline()){
                    firestoreQueries.getDebtCollectionQuery(user)
                        .whereEqualTo("traderId", traderId)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .limit(5)
                        .get().await().toObjects(Debt::class.java)
            }else{
                debtsDao.getDebtsWithId(traderId)
            }
            RepoResult.Success(data)
        }catch (e:Exception){
            RepoResult.Error(e)
        }
    }

    override suspend fun registerNewDebtDatabase(debt: Debt): SimpleResult = withContext(Dispatchers.IO) {
        val user = currentUser()
        try {
            val debtRef = firestoreQueries.getDebtCollectionQuery(user)
            debt.debtId = debtRef.document().id
            debt.author = firestoreQueries.getCurrentUserEmail()
            debt.businessId = user.businessId

            if (user.isBusinessOnline()){
                firestore.runBatch {
                    val updateCounterRef = firestoreQueries.getBusinessCountersQuery(user)
                    if (debt.type == "CLIENT"){
                        val updateClientRef = firestoreQueries.getClientsCollectionQuery(user)
                        it.update(updateClientRef.document(debt.traderId), "debt",FieldValue.increment(debt.amount.toLong()))
                        it.update(updateCounterRef,"clientsDebt", FieldValue.increment(debt.amount.toLong()))
                    }else{
                        val updateSupplierRef = firestoreQueries.getSuppliersCollectionQuery(user)
                        it.update(updateSupplierRef.document(debt.traderId), "debt",FieldValue.increment(debt.amount.toLong()))
                        it.update(updateCounterRef,"suppliersDebt", FieldValue.increment(debt.amount.toLong()))
                    }
                    it.set(debtRef.document(debt.debtId), debt)
                }.await()
            }else{
                if (debt.type == "CLIENT"){
                    debtsDao.updateClientDebt(debt.traderId, debt.amount)
                    debtsDao.updateClientsDebt(debt.amount)
                }else{
                    debtsDao.updateSupplierDebt(debt.traderId, debt.amount)
                    debtsDao.updateSupplierDebt(debt.amount)
                }
                debtsDao.insert(debt)
            }
            SimpleResult.Success
        }catch (e:Exception){
            SimpleResult.Failure
        }
    }

    override suspend fun getBusinessDebtData(): BusinessDebtsData = withContext(Dispatchers.IO){
        val user = currentUser()
        if (user.isBusinessOnline()){
            firestoreQueries.getBusinessCountersQuery(user)
                .get().await().toObject(BusinessDebtsData::class.java) ?: BusinessDebtsData(0f,0f)
        }else{
            debtsDao.getDebtsForBusiness()
        }
    }

    override suspend fun getBusinessDebtsPagingDataFlow(): Flow<PagingData<Debt>> = withContext(Dispatchers.IO){
        val user = currentUser()
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200                )
        ) {
            if (user.isBusinessOnline()){
                val query = firestoreQueries.getDebtCollectionQuery(user)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                FirestoreDebtsPagingSource(query)
            }
            else{ debtsDao.getPagedDebts() }
        }.flow
    }

    override suspend fun getClientPagingDataFlow(): Flow<PagingData<Client>> = withContext(Dispatchers.IO){
        val user = currentUser()
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200                )
        ) {
            if (user.isBusinessOnline()){
                val query = firestoreQueries.getClientsCollectionQuery(user)
                    .whereNotEqualTo("debt", 0)
                FirestoreClientsPagingSource(query)
            }
            else{ debtsDao.getClientDebtsPaged() }
        }.flow
    }

    override suspend fun getSupplierPagingDataFlow(): Flow<PagingData<Supplier>> = withContext(Dispatchers.IO){
        val user = currentUser()
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200                )
        ) {
            if(user.isBusinessOnline()){
                val query = firestoreQueries.getSuppliersCollectionQuery(user)
                    .whereNotEqualTo("debt", 0)
                FirestoreSuppliersPagingSource(query)
            }
            else{ debtsDao.getSupplierDebtsPaged() }
        }.flow
    }

}