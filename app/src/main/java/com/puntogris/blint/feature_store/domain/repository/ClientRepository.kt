package com.puntogris.blint.feature_store.domain.repository

import androidx.paging.PagingData
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.domain.model.Client
import com.puntogris.blint.feature_store.domain.model.order.Record
import kotlinx.coroutines.flow.Flow

interface ClientRepository {

    suspend fun saveClient(client: Client): SimpleResult

    suspend fun deleteClient(clientId: String): SimpleResult

    suspend fun getClient(clientId: String): Client

    fun getClientsPaged(query: String? = null): Flow<PagingData<Client>>

    fun getClientRecordsPaged(clientId: String): Flow<PagingData<Record>>
}