package com.puntogris.blint.data.repository.events

import androidx.paging.PagingData
import com.puntogris.blint.model.Event
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow

interface IEventRepository {

    fun getEventsPaged(filter: String): Flow<PagingData<Event>>

    suspend fun saveEvent(event: Event): SimpleResult

    suspend fun deleteEvent(eventId: String): SimpleResult

    suspend fun updateEventStatus(event: Event): SimpleResult
}