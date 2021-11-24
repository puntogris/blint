package com.puntogris.blint.domain.repository

import androidx.paging.PagingData
import com.puntogris.blint.model.Event
import com.puntogris.blint.utils.types.EventStatus
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow

interface EventRepository {

    fun getBusinessLastEventsDatabase(): Flow<List<Event>>

    fun getEventsPaged(eventStatus: EventStatus): Flow<PagingData<Event>>

    suspend fun saveEvent(event: Event): SimpleResult

    suspend fun deleteEvent(eventId: String): SimpleResult

    suspend fun updateEventStatus(event: Event): SimpleResult
}