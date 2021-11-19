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
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClientRepository @Inject constructor(
    private val clientsDao: ClientsDao,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao,
    private val ordersDao: OrdersDao,
    private val dispatcher: DispatcherProvider
) : IClientRepository {

    override suspend fun saveClient(client: Client): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {

                if (client.clientId == 0) {
                    client.businessId = usersDao.getCurrentBusinessId()
                    statisticsDao.incrementTotalClients()
                }

                clientsDao.insert(client)
            }
        }

    override fun getClientsPaged(query: String?): Flow<PagingData<Client>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            if (query.isNullOrBlank()) clientsDao.getClientsPaged()
            else clientsDao.getClientsSearchPaged("%$query%")
        }.flow
    }

    override suspend fun deleteClient(clientId: Int): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                clientsDao.delete(clientId)
                statisticsDao.decrementTotalClients()
            }
        }

    override fun getClientRecordsPaged(clientId: Int): Flow<PagingData<Record>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            ordersDao.getClientsRecords(clientId)
        }.flow
    }
}