package com.puntogris.blint.data.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.EventsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.model.Event
import com.puntogris.blint.utils.EventsDashboard
import com.puntogris.blint.utils.RepoResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val usersDao: UsersDao,
    private val eventsDao: EventsDao,
    private val firestoreQueries: FirestoreQueries
): IMainRepository {

    private val firestore = Firebase.firestore
    private suspend fun currentBusiness() = usersDao.getUser()

    override suspend fun getLastBusinessEvents(): EventsDashboard = withContext(Dispatchers.IO) {
        try {
            val user = currentBusiness()
            val events =
                if (user.currentBusinessIsOnline()){
                    val query = firestoreQueries.getEventsCollectionQuery(user).limit(3).get().await()
                    query.toObjects(Event::class.java)
                }else{
                    eventsDao.getLastThreeEvents()
                }
            if (events.isNotEmpty()) EventsDashboard.Success(events)
            else EventsDashboard.DataNotFound
        }catch (e:Exception){
            EventsDashboard.Error(e)
        }
    }
}