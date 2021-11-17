package com.puntogris.blint.data.repository.clients

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.data_source.local.dao.ClientsDao
import com.puntogris.blint.data.data_source.local.dao.OrdersDao
import com.puntogris.blint.data.data_source.local.dao.StatisticsDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClientRepository @Inject constructor(
    private val clientsDao: ClientsDao,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao,
    private val ordersDao: OrdersDao,
) : IClientRepository {

    private suspend fun currentBusiness() = usersDao.getCurrentBusinessFromUser()

    override suspend fun saveClientDatabase(client: Client): SimpleResult =
        withContext(Dispatchers.IO) {
            try {
                val isNewClient = client.clientId == 0
                val user = currentBusiness()

                if (isNewClient) {
                    client.apply {
                        businessId = user.businessId
                    }
                }

                clientsDao.insert(client)
                if (isNewClient) statisticsDao.incrementTotalClients()

                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override fun getAllClientsPaged(): Flow<PagingData<Client>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            clientsDao.getAllPaged()
        }.flow
    }

    override suspend fun deleteClientDatabase(clientId: Int): SimpleResult =
        withContext(Dispatchers.IO) {
            try {
                clientsDao.delete(clientId)
                statisticsDao.decrementTotalClients()

                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override suspend fun getClientRecordsPagingDataFlow(clientId: Int): Flow<PagingData<Record>> =
        withContext(Dispatchers.IO) {
            Pager(
                PagingConfig(
                    pageSize = 30,
                    enablePlaceholders = true,
                    maxSize = 200
                )
            ) {
                ordersDao.getClientsRecords(clientId)
            }.flow
        }

    override fun getClientsWithNamePaged(name: String): Flow<PagingData<Client>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            clientsDao.getPagedSearch("%${name}%")
        }.flow
    }

}