package com.puntogris.blint.data.repository.main

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.data_source.local.dao.EmployeeDao
import com.puntogris.blint.data.data_source.local.dao.EventsDao
import com.puntogris.blint.data.data_source.local.dao.StatisticsDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.data.data_source.remote.FirestoreQueries
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
    private val statisticsDao: StatisticsDao,
    private val employeeDao: EmployeeDao
): IMainRepository {

    private val auth = FirebaseAuth.getInstance()

    private val firestore = Firebase.firestore

    private suspend fun currentBusiness() = usersDao.getCurrentBusinessFromUser()

    override fun checkIfUserIsLogged() = auth.currentUser != null

    override suspend fun updateCurrentBusiness(id: String) {
        usersDao.updateUserCurrentBusiness(id)
    }

    override fun getCurrentUserFlow() = usersDao.getUserFlow()

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


    override suspend fun checkIfAccountIsSynced(employee: List<Employee>): AccountStatus = withContext(Dispatchers.IO){
        try {
            val roomEmployees = getBusinessListRoom()
            if (roomEmployees.toSet() == employee.toSet()) {
                if (employee.isEmpty()) AccountStatus.Synced(false)
                else AccountStatus.Synced(true)
            }
            else AccountStatus.OutOfSync(employee)
        }catch (e:Exception){
            AccountStatus.Error
        }
    }

    override suspend fun getBusinessLastEventsDatabase(): EventsDashboard = withContext(Dispatchers.IO) {
        try {
            val events = eventsDao.getLastThreeEvents()
            if (events.isNotEmpty()) EventsDashboard.Success(events)
            else EventsDashboard.DataNotFound
        }catch (e:Exception){
            EventsDashboard.Error(e)
        }
    }
    override suspend fun getBusinessCounterFlow(): Flow<BusinessCounters> = withContext(Dispatchers.IO) {
        statisticsDao.getBusinessStatisticsFlow()
    }
}
