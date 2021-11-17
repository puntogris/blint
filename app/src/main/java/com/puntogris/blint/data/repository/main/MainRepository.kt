package com.puntogris.blint.data.repository.main

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.data_source.local.dao.BusinessDao
import com.puntogris.blint.data.data_source.local.dao.EventsDao
import com.puntogris.blint.data.data_source.local.dao.StatisticsDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.BusinessCounters
import com.puntogris.blint.utils.types.AccountStatus
import com.puntogris.blint.utils.types.EventsDashboard
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
    private val businessDao: BusinessDao
) : IMainRepository {

    private val auth = FirebaseAuth.getInstance()

    private val firestore = Firebase.firestore

    private suspend fun currentBusiness() = usersDao.getCurrentBusinessFromUser()

    override fun checkIfUserIsLogged() = auth.currentUser != null

    override suspend fun updateCurrentBusiness(id: String) {
        usersDao.updateUserCurrentBusiness(id)
    }

    override fun getCurrentUserFlow() = usersDao.getUserFlow()

    override suspend fun getBusinessListRoom() = businessDao.getBusiness()

    override fun getBusinessesStatus(): Flow<RepoResult<List<Business>>> = callbackFlow {
        val ref = firestore.collectionGroup("employees")
            .whereEqualTo("employeeId", auth.currentUser?.uid.toString())
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
        withContext(Dispatchers.IO) {
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
