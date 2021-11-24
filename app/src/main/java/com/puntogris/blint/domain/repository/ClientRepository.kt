package com.puntogris.blint.domain.repository

import androidx.paging.PagingData
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.order.Record
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow

interface ClientRepository {

    suspend fun saveClient(client: Client): SimpleResult

    suspend fun deleteClient(clientId: String): SimpleResult

    fun getClientsPaged(query: String? = null): Flow<PagingData<Client>>

    fun getClientRecordsPaged(clientId: String): Flow<PagingData<Record>>
}