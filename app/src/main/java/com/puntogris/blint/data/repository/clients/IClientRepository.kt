package com.puntogris.blint.data.repository.clients

import androidx.paging.PagingData
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IClientRepository {

    suspend fun saveClient(client: Client): SimpleResult

    suspend fun deleteClient(clientId: Int): SimpleResult

    fun getClientsPaged(): Flow<PagingData<Client>>

    fun getClientRecordsPaged(clientId: Int): Flow<PagingData<Record>>

    fun getClientsWithQueryPaged(query: String): Flow<PagingData<Client>>
}