package com.puntogris.blint.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.DebtsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.FirestoreClientsPagingSource
import com.puntogris.blint.data.remote.FirestoreDebtsPagingSource
import com.puntogris.blint.data.repo.irepo.IDebtsRepository
import com.puntogris.blint.model.BusinessDebtsData
import com.puntogris.blint.model.Debt
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
    private val usersDao: UsersDao
):IDebtsRepository {

    private suspend fun currentUser() = usersDao.getUser()
    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    override suspend fun getLastTraderDebts(traderId: String): RepoResult<List<Debt>> {
        val user = currentUser()
        return try {
            val data =
                if (user.currentBusinessIsOnline()){
                firestore
                    .collection("users")
                    .document(user.currentBusinessOwner)
                    .collection("business")
                    .document(user.currentBusinessId)
                    .collection("debts")
                    .whereEqualTo("traderId", traderId)
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
            if (user.currentBusinessIsOnline()){
                val debtRef =
                firestore.collection("users")
                    .document(user.currentBusinessOwner)
                    .collection("business")
                    .document(user.currentBusinessId)
                    .collection("debts")

                debt.debtId = debtRef.document().id
                debt.author = auth.currentUser?.email.toString()

                firestore.runBatch {
                    val updateRef =
                        if (debt.type == "CLIENT"){
                            firestore.collection("users")
                                .document(user.currentBusinessOwner)
                                .collection("business")
                                .document(user.currentBusinessId)
                                .collection("clients")

                        }else{
                            firestore.collection("users")
                                .document(user.currentBusinessOwner)
                                .collection("business")
                                .document(user.currentBusinessId)
                                .collection("suppliers")
                        }
                    it.update(updateRef.document(debt.traderId), "debt",FieldValue.increment(debt.amount.toLong()))
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
            }
            SimpleResult.Success
        }catch (e:Exception){
            SimpleResult.Failure
        }
    }

    override suspend fun getBusinessDebtData(): BusinessDebtsData = withContext(Dispatchers.IO){
        val user = currentUser()
        if (user.currentBusinessIsOnline()){
            firestore
                .collection("users")
                .document(user.currentBusinessOwner)
                .collection("business")
                .document(user.currentBusinessId)
                .collection("counters")
                .document("counter")
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
            if (user.currentBusinessIsOnline()){
                val query = firestore
                    .collection("users")
                    .document(user.currentBusinessOwner)
                    .collection("business")
                    .document(user.currentBusinessId)
                    .collection("debts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                FirestoreDebtsPagingSource(query)
            }
            else{ debtsDao.getPagedDebts() }
        }.flow
    }
}