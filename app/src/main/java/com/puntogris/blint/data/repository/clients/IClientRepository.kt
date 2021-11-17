package com.puntogris.blint.data.repository.clients

import androidx.paging.PagingData
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IClientRepository {
    suspend fun saveClientDatabase(client: Client): SimpleResult
    fun getAllClientsPaged(): Flow<PagingData<Client>>
    suspend fun deleteClientDatabase(clientId: Int): SimpleResult
    fun getClientRecordsPagingDataFlow(clientId: Int): Flow<PagingData<Record>>
    fun getClientsWithNamePaged(name: String): Flow<PagingData<Client>>
}