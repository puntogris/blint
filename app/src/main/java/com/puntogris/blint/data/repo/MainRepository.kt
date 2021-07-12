package com.puntogris.blint.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.puntogris.blint.data.local.dao.EventsDao
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.FirestoreQueries
import com.puntogris.blint.data.repo.irepo.IMainRepository
import com.puntogris.blint.model.BusinessCounters
import com.puntogris.blint.model.Event
import com.puntogris.blint.utils.EventsDashboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val usersDao: UsersDao,
    private val eventsDao: EventsDao,
    private val firestoreQueries: FirestoreQueries,
    private val statisticsDao: StatisticsDao
): IMainRepository {

    private val auth = FirebaseAuth.getInstance()

    private suspend fun currentBusiness() = usersDao.getUser()

    override fun checkIfUserIsLogged() = auth.currentUser != null

    override suspend fun getBusinessLastEventsDatabase(): EventsDashboard = withContext(Dispatchers.IO) {
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

    override fun getAllUnreadNotifications(): StateFlow<Int> =
        MutableStateFlow(0).also { stateFlow ->
            firestoreQueries.getAllUnreadNotificationsQuery()
                .addSnapshotListener { snap, _ ->
                    snap?.documents?.let { docs ->
                        stateFlow.value = docs.size
                    }
                }
        }

    @ExperimentalCoroutinesApi
    override suspend fun getBusinessCounterFlow(): Flow<BusinessCounters> = withContext(Dispatchers.IO) {
        val user = currentBusiness()
        return@withContext if (user.currentBusinessIsOnline()){
            callbackFlow {
                val ref = firestoreQueries.getBusinessCollectionQuery(user).addSnapshotListener { doc, _ ->
                    if (doc != null) {
                        doc.toObject(BusinessCounters::class.java)?.let {
                            trySend(it)
                        }
                    }
                }
                awaitClose { ref.remove() }
            }
        } else{ statisticsDao.getBusinessStatisticsFlow() }
    }
}