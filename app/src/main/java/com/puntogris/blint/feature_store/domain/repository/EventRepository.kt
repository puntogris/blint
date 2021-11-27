package com.puntogris.blint.feature_store.domain.repository

import androidx.paging.PagingData
import com.puntogris.blint.common.utils.types.EventStatus
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {

    fun getBusinessLastEventsDatabase(): Flow<List<Event>>

    fun getEventsPaged(eventStatus: EventStatus): Flow<PagingData<Event>>

    suspend fun saveEvent(event: Event): SimpleResult

    suspend fun deleteEvent(eventId: String): SimpleResult

    suspend fun updateEventStatus(event: Event): SimpleResult
}