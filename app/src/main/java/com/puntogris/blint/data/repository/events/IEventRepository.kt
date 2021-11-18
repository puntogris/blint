package com.puntogris.blint.data.repository.events

import androidx.paging.PagingData
import com.puntogris.blint.model.Event
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IEventRepository {
    suspend fun createEventDatabase(event: Event): SimpleResult

    fun getEventPagingDataFlow(filter: String): Flow<PagingData<Event>>

    suspend fun deleteEventDatabase(eventId: String): SimpleResult

    suspend fun updateEventStatusDatabase(event: Event): SimpleResult
}