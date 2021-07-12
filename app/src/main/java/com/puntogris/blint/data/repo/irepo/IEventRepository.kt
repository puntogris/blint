package com.puntogris.blint.data.repo.irepo

import androidx.paging.PagingData
import com.puntogris.blint.model.Event
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IEventRepository {
    suspend fun createEventDatabase(event:Event):SimpleResult
    suspend fun getEventPagingDataFlow(filter: String): Flow<PagingData<Event>>
    suspend fun deleteEventDatabase(eventId: String): SimpleResult
    suspend fun updateEventStatusDatabase(event: Event): SimpleResult
}