package com.puntogris.blint.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.data_source.local.dao.EventsDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.domain.repository.EventRepository
import com.puntogris.blint.model.Event
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.EventStatus
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class EventRepositoryImpl(
    private val eventsDao: EventsDao,
    private val usersDao: UsersDao,
    private val dispatcher: DispatcherProvider
) : EventRepository {

    override fun getBusinessLastEventsDatabase() =
        eventsDao.getLastThreeEventsFlow().flowOn(dispatcher.io)

    override suspend fun saveEvent(event: Event): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                event.businessId = usersDao.getCurrentBusinessId()
                eventsDao.insert(event)
            }
        }

    override fun getEventsPaged(eventStatus: EventStatus): Flow<PagingData<Event>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            if (eventStatus == EventStatus.All) eventsDao.getEventsPaged()
            else eventsDao.getEventsWithStatusPaged(eventStatus.value)
        }.flow
    }

    override suspend fun deleteEvent(eventId: String): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                eventsDao.delete(eventId)
            }
        }

    override suspend fun updateEventStatus(event: Event): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                eventsDao.updateEvent(event)
            }
        }
}