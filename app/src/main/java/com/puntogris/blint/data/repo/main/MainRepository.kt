package com.puntogris.blint.data.repo.main

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.EmployeeDao
import com.puntogris.blint.data.local.dao.EventsDao
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.FirestoreQueries
import com.puntogris.blint.model.BusinessCounters
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.Event
import com.puntogris.blint.utils.AccountStatus
import com.puntogris.blint.utils.EventsDashboard
import com.puntogris.blint.utils.RepoResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val usersDao: UsersDao,
    private val eventsDao: EventsDao,
    private val firestoreQueries: FirestoreQueries,
    private val statisticsDao: StatisticsDao,
    private val employeeDao: EmployeeDao
): IMainRepository {

    private val auth = FirebaseAuth.getInstance()

    private val firestore = Firebase.firestore

    private suspend fun currentBusiness() = usersDao.getCurrentBusiness()

    override fun checkIfUserIsLogged() = auth.currentUser != null

    override suspend fun updateCurrentBusiness(id: String) {
        usersDao.updateCurrentBusiness(id)
    }

    override fun getCurrentUserFlow() = usersDao.getUserFlow()

    override fun getUserLiveDataRoom() = usersDao.getUserLiveData()

    override suspend fun getBusinessListRoom() = employeeDao.getEmployeesList()

    @ExperimentalCoroutinesApi
    override fun getBusinessesStatus(): Flow<RepoResult<List<Employee>>>  = callbackFlow {
            val ref = firestore.collectionGroup("employees")
                .whereEqualTo("employeeId", auth.currentUser?.uid.toString())
                .addSnapshotListener { value, error ->
                    if (value != null){
                        val businesses = value.toObjects(Employee::class.java)
                        businesses.removeIf { it.businessStatus == "DELETED" }
                        trySend(RepoResult.Success(businesses))
                    }else{
                        if (error != null) trySend(RepoResult.Error(error))
                    }
                }
            awaitClose { ref.remove() }
        }



    @ExperimentalCoroutinesApi
    suspend fun getBusinessaesStatus(): Flow<RepoResult<List<Employee>>>  = callbackFlow {
        val ref = firestore.collectionGroup("employees")
            .whereEqualTo("employeeId", auth.currentUser?.uid.toString())
            .addSnapshotListener { value, error ->

                if (value != null){
                    val businesses = value.toObjects(Employee::class.java)
                    businesses.removeIf { it.businessStatus == "DELETED" }
                    trySend(RepoResult.Success(businesses))
                }else{
                    if (error != null) trySend(RepoResult.Error(error))
                }
            }
        awaitClose { ref.remove() }
    }

    override suspend fun checkIfAccountIsSynced(employee: List<Employee>): AccountStatus = withContext(Dispatchers.IO){
        try {
            val roomEmployees = getBusinessListRoom()
            if (roomEmployees.toSet() == employee.toSet()) {
                if (employee.isEmpty()) AccountStatus.Synced(false) else AccountStatus.Synced(true)
            }
            else AccountStatus.OutOfSync(employee)
        }catch (e:Exception){
            AccountStatus.Error
        }
    }

    override suspend fun getBusinessLastEventsDatabase(): EventsDashboard = withContext(Dispatchers.IO) {
        try {
            val user = currentBusiness()
            val events =
                if (user.isBusinessOnline()){
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
        return@withContext if (user.isBusinessOnline()){
            callbackFlow {
                val ref = firestoreQueries.getBusinessCountersQuery(user).addSnapshotListener { doc, _ ->
                    if (doc != null) {
                        doc.toObject(BusinessCounters::class.java)?.let {
                            trySend(it)
                        }
                    }
                }
                awaitClose { ref.remove() }
            }
        } else{
            statisticsDao.getBusinessStatisticsFlow()
        }
    }
}