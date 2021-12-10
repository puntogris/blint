package com.puntogris.blint.feature_store.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.types.EventStatus
import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.feature_store.data.data_source.local.dao.EventsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.UsersDao
import com.puntogris.blint.feature_store.domain.model.Event
import com.puntogris.blint.feature_store.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class EventRepositoryImpl(
    private val eventsDao: EventsDao,
    private val usersDao: UsersDao,
    private val dispatcher: DispatcherProvider
) : EventRepository {

    override fun getBusinessLastEvents() =
        eventsDao.getLastThreeEventsFlow().flowOn(dispatcher.io)

    override suspend fun saveEvent(event: Event): SimpleResource =
        withContext(dispatcher.io) {
            SimpleResource.build {
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

    override suspend fun deleteEvent(eventId: String): SimpleResource =
        withContext(dispatcher.io) {
            SimpleResource.build {
                eventsDao.delete(eventId)
            }
        }

    override suspend fun updateEventStatus(event: Event): SimpleResource =
        withContext(dispatcher.io) {
            SimpleResource.build {
                eventsDao.updateEvent(event)
            }
        }
}