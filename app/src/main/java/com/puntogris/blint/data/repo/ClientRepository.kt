package com.puntogris.blint.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.ClientsDao
import com.puntogris.blint.data.local.dao.OrdersDao
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.FirestoreClientsPagingSource
import com.puntogris.blint.data.remote.FirestoreQueries
import com.puntogris.blint.data.remote.FirestoreRecordsPagingSource
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClientRepository @Inject constructor(
    private val clientsDao: ClientsDao,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao,
    private val ordersDao: OrdersDao,
    private val firestoreQueries: FirestoreQueries
): IClientRepository {

    private val firestore = Firebase.firestore

    private suspend fun currentBusiness() = usersDao.getUser()

    override suspend fun saveClientDatabase(client: Client): SimpleResult = withContext(Dispatchers.IO){
        try {
            val user = currentBusiness()
            val clientRef = firestoreQueries.getClientsCollectionQuery(user).document()
            client.apply {
                businessId = user.currentBusinessId
                clientId = clientRef.id
            }
            if (user.currentBusinessIsOnline()){
                val clientRefCounter = firestoreQueries.getBusinessCollectionQuery(user)
                firestore.runBatch {
                    it.set(clientRef, client)
                    if (client.clientId == "")
                        it.update(clientRefCounter, "clients_counter", FieldValue.increment(1))
                }.await()
            }else{
                clientsDao.insert(client)
                if (client.clientId == "") statisticsDao.incrementTotalClients()
            }
            SimpleResult.Success
        }catch (e:Exception){ SimpleResult.Failure }
    }

    override suspend fun getClientPagingDataFlow(): Flow<PagingData<Client>> = withContext(Dispatchers.IO){
        val user = currentBusiness()
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200                )
        ) {
            if (user.currentBusinessIsOnline()){
                val query = firestoreQueries.getClientsCollectionQuery(user)
                FirestoreClientsPagingSource(query)
            }
            else{ clientsDao.getAllPaged() }
        }.flow
    }

    override suspend fun deleteClientDatabase(clientId: String): SimpleResult = withContext(Dispatchers.IO){
        try {
            val user = currentBusiness()
            if (user.currentBusinessIsOnline()){
                firestoreQueries.getClientsCollectionQuery(user)
                    .document(clientId)
                    .delete()
                    .await()
            }else{
                clientsDao.delete(clientId)
                statisticsDao.decrementTotalClients()
            }
            SimpleResult.Success
        }catch (e:Exception){ SimpleResult.Failure }
    }

    override suspend fun getClientRecordsPagingDataFlow(clientId: String): Flow<PagingData<Record>> = withContext(Dispatchers.IO) {
        val user = currentBusiness()
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200                )
        ) {
            if (user.currentBusinessIsOnline()){
                val query = firestoreQueries.getRecordsWithTraderIdQuery(user, clientId)
                FirestoreRecordsPagingSource(query)
            }
            else{ ordersDao.getClientsRecords(clientId) }
        }.flow
    }

    override suspend fun getClientWithNamePagingDataFlow(name: String): Flow<PagingData<Client>> = withContext(Dispatchers.IO) {
        val user = currentBusiness()
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200                )
        ) {
            if (user.currentBusinessIsOnline()){
                val query = firestoreQueries
                    .getClientsCollectionQuery(user)
                    .whereArrayContains("search_name", name)
                    .limit(5)

                FirestoreClientsPagingSource(query)
            }
            else{ clientsDao.getPagedSearch("%${name}%") }
        }.flow
    }

}