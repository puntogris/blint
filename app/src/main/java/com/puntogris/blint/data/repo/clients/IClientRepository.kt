package com.puntogris.blint.data.repo.clients

import androidx.paging.PagingData
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IClientRepository{
    suspend fun saveClientDatabase(client: Client): SimpleResult
    suspend fun getClientPagingDataFlow(): Flow<PagingData<Client>>
    suspend fun deleteClientDatabase(clientId: String): SimpleResult
    suspend fun getClientRecordsPagingDataFlow(clientId: String): Flow<PagingData<Record>>
    suspend fun getClientWithNamePagingDataFlow(name:String): Flow<PagingData<Client>>
}