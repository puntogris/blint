package com.puntogris.blint.data.repository.events

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.data_source.local.dao.EventsDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.model.Event
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val eventsDao: EventsDao,
    private val usersDao: UsersDao
) : IEventRepository {


    override suspend fun createEventDatabase(event: Event): SimpleResult =
        withContext(Dispatchers.IO) {
            SimpleResult.build {
                event.businessId = usersDao.getCurrentBusinessId()
                eventsDao.insert(event)
            }
        }

    override suspend fun getEventPagingDataFlow(filter: String): Flow<PagingData<Event>> =
        withContext(Dispatchers.IO) {
            Pager(
                PagingConfig(
                    pageSize = 30,
                    enablePlaceholders = true,
                    maxSize = 200
                )
            ) {
                if (filter == "ALL") eventsDao.getAllPaged()
                else eventsDao.getPagedEventsWithFilter(filter)
            }.flow
        }

    override suspend fun deleteEventDatabase(eventId: String): SimpleResult =
        withContext(Dispatchers.IO) {
            SimpleResult.build {
                eventsDao.delete(eventId)
            }
        }

    override suspend fun updateEventStatusDatabase(event: Event): SimpleResult =
        withContext(Dispatchers.IO) {
            SimpleResult.build {
                eventsDao.updateEvent(event)
            }
        }
}