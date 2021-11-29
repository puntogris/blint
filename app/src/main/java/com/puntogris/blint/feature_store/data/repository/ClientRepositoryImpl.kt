package com.puntogris.blint.feature_store.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.UUIDGenerator
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.data.data_source.local.dao.*
import com.puntogris.blint.feature_store.domain.model.Client
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ClientRepositoryImpl(
    private val clientsDao: ClientsDao,
    private val usersDao: UsersDao,
    private val recordsDao: RecordsDao,
    private val businessDao: BusinessDao,
    private val dispatcher: DispatcherProvider
) : ClientRepository {

    override suspend fun saveClient(client: Client): SimpleResult = withContext(dispatcher.io) {
        SimpleResult.build {
            if (client.clientId.isBlank()) {
                client.clientId = UUIDGenerator.randomUUID()
                client.businessId = usersDao.getCurrentBusinessId()
                businessDao.incrementTotalClients()
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

    override suspend fun deleteClient(clientId: String): SimpleResult = withContext(dispatcher.io) {
        SimpleResult.build {
            clientsDao.delete(clientId)
            businessDao.decrementTotalClients()
        }
    }

    override suspend fun getClient(clientId: String): Client = withContext(dispatcher.io) {
        clientsDao.getClient(clientId)
    }

    override suspend fun getClients() = clientsDao.getClients()

    override fun getClientRecordsPaged(clientId: String): Flow<PagingData<Record>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) { recordsDao.getClientsRecords(clientId) }.flow
    }
}