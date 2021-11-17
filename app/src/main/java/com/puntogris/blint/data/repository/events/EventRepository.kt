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

    private suspend fun currentUser() = usersDao.getCurrentBusinessFromUser()

    override suspend fun createEventDatabase(event: Event): SimpleResult =
        withContext(Dispatchers.IO) {
            try {
                val user = currentUser()
                event.apply {
                    //todo hook the current user to the event with an id
                }
                eventsDao.insert(event)

                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
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
            try {
                eventsDao.delete(eventId)
                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override suspend fun updateEventStatusDatabase(event: Event): SimpleResult =
        withContext(Dispatchers.IO) {
            try {
                eventsDao.updateEvent(event)
                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }
}