package com.puntogris.blint.data.repository.main

import com.puntogris.blint.data.data_source.local.dao.BusinessDao
import com.puntogris.blint.data.data_source.local.dao.EventsDao
import com.puntogris.blint.data.data_source.local.dao.StatisticsDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.data.data_source.remote.FirebaseClients
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.BusinessCounters
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.AccountStatus
import com.puntogris.blint.utils.types.RepoResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val usersDao: UsersDao,
    private val eventsDao: EventsDao,
    private val statisticsDao: StatisticsDao,
    private val businessDao: BusinessDao,
    private val dispatcher: DispatcherProvider,
    private val firebase: FirebaseClients
) : IMainRepository {

    override fun checkIfUserIsLogged() = firebase.auth.currentUser != null

    override fun getCurrentUserFlow() = usersDao.getUserFlow()

    override suspend fun getBusinessListRoom() = businessDao.getBusiness()

    override fun getBusinessesStatus(): Flow<RepoResult<List<Business>>> = callbackFlow {
        val ref = firebase.firestore.collectionGroup("employees")
            .whereEqualTo("employeeId", firebase.auth.currentUser?.uid.toString())
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val businesses = value.toObjects(Business::class.java)
                    businesses.removeIf { it.status == "DELETED" }
                    trySend(RepoResult.Success(businesses))
                } else {
                    if (error != null) trySend(RepoResult.Error(error))
                }
            }
        awaitClose { ref.remove() }
    }

    override suspend fun checkIfAccountIsSynced(business: List<Business>): AccountStatus =
        withContext(dispatcher.io) {
            try {
                val roomEmployees = getBusinessListRoom()
                if (roomEmployees.toSet() == business.toSet()) {
                    if (business.isEmpty()) AccountStatus.Synced(false)
                    else AccountStatus.Synced(true)
                } else AccountStatus.OutOfSync(business)
            } catch (e: Exception) {
                AccountStatus.Error
            }
        }

    override fun getBusinessLastEventsDatabase() = eventsDao.getLastThreeEventsFlow()


    override suspend fun getBusinessCounterFlow(): Flow<BusinessCounters> =
        withContext(Dispatchers.IO) {
            statisticsDao.getBusinessStatisticsFlow()
        }
}
