package com.puntogris.blint.data.repo.events

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.local.dao.EventsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.FirestoreEventsPagingSource
import com.puntogris.blint.data.remote.FirestoreQueries
import com.puntogris.blint.model.Event
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val eventsDao: EventsDao,
    private val usersDao: UsersDao,
    private val firestoreQueries: FirestoreQueries
): IEventRepository {

    private suspend fun currentUser() = usersDao.getCurrentBusiness()

    override suspend fun createEventDatabase(event: Event): SimpleResult = withContext(Dispatchers.IO){
        try {
            val user = currentUser()
            val eventRef = firestoreQueries.getEventsCollectionQuery(user).document()
            event.apply {
                eventId = eventRef.id
                businessId = user.businessId
            }
            if (user.isBusinessOnline()) eventRef.set(event).await()
            else eventsDao.insert(event)

            SimpleResult.Success
        }catch (e:Exception){ SimpleResult.Failure }
    }

    override suspend fun getEventPagingDataFlow(filter: String): Flow<PagingData<Event>> = withContext(Dispatchers.IO){
        val user = currentUser()
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200)
        ) {
            if(user.isBusinessOnline()){
                val query = firestoreQueries.getEventsCollectionQuery(user)

                if (filter == "ALL") FirestoreEventsPagingSource(query)
                else FirestoreEventsPagingSource(query.whereEqualTo("status", filter))

            }
            else{
                if (filter == "ALL") eventsDao.getAllPaged()
                else eventsDao.getPagedEventsWithFilter(filter)
            }
        }.flow
    }

    override suspend fun deleteEventDatabase(eventId: String): SimpleResult = withContext(Dispatchers.IO){
        try {
            val user = currentUser()
            if (user.isBusinessOnline()){
                firestoreQueries.getEventsCollectionQuery(user)
                    .document(eventId)
                    .delete()
            }else{ eventsDao.delete(eventId) }
            SimpleResult.Success
        }catch (e:Exception){ SimpleResult.Failure }
    }

    override suspend fun updateEventStatusDatabase(event: Event): SimpleResult = withContext(Dispatchers.IO) {
        try {
            val user = currentUser()
            if (user.isBusinessOnline()){
                firestoreQueries.getEventsCollectionQuery(user).document(event.eventId).set(event)
            }else{ eventsDao.updateEvent(event) }
            SimpleResult.Success
        }catch (e:Exception){ SimpleResult.Failure }
    }
}