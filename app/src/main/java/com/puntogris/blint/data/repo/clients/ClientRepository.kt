package com.puntogris.blint.data.repo.clients

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
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
import com.puntogris.blint.ui.SharedPref
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
    private val firestoreQueries: FirestoreQueries,
): IClientRepository {

    private val firestore = Firebase.firestore

    private suspend fun currentBusiness() = usersDao.getCurrentBusiness()

    override suspend fun saveClientDatabase(client: Client): SimpleResult = withContext(Dispatchers.IO){
        try {
            val isNewClient = client.clientId.isEmpty()
            val user = currentBusiness()
            val clientRef = firestoreQueries.getClientsCollectionQuery(user)
            if (isNewClient){
                client.apply {
                    businessId = user.businessId
                    clientId = clientRef.document().id
                }
            }
            if (user.isBusinessOnline()){
                val clientRefCounter = firestoreQueries.getBusinessCountersQuery(user)
                firestore.runBatch {
                    it.set(clientRef.document(client.clientId), client)
                    if (isNewClient)
                        it.update(clientRefCounter, "totalClients", FieldValue.increment(1))
                }.await()
            }else{
                clientsDao.insert(client)
                if (isNewClient) statisticsDao.incrementTotalClients()
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
            if (user.isBusinessOnline()){
                val query = firestoreQueries.getClientsCollectionQuery(user).orderBy("name", Query.Direction.ASCENDING)
                FirestoreClientsPagingSource(query)
            }
            else{ clientsDao.getAllPaged() }
        }.flow
    }

    override suspend fun deleteClientDatabase(clientId: String): SimpleResult = withContext(Dispatchers.IO){
        try {
            val user = currentBusiness()
            if (user.isBusinessOnline()){
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
            if (user.isBusinessOnline()){
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
            if (user.isBusinessOnline()){
                val query = firestoreQueries
                    .getClientsCollectionQuery(user)
                    .whereArrayContains("search_name", name.lowercase())
                    .limit(5)

                FirestoreClientsPagingSource(query)
            }
            else{ clientsDao.getPagedSearch("%${name}%") }
        }.flow
    }

}