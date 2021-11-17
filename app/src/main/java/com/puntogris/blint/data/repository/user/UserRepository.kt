package com.puntogris.blint.data.repository.user

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.data_source.local.dao.BusinessDao
import com.puntogris.blint.data.data_source.local.dao.StatisticsDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.model.*
import com.puntogris.blint.ui.SharedPreferences
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.SUGGESTION_COLLECTION_NAME
import com.puntogris.blint.utils.Constants.LOCAL
import com.puntogris.blint.utils.Constants.REPORT_FIELD_FIRESTORE
import com.puntogris.blint.utils.Constants.TIMESTAMP_FIELD_FIRESTORE
import com.puntogris.blint.utils.Constants.USERS_COLLECTION
import com.puntogris.blint.utils.Constants.USER_ID_FIELD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val businessDao: BusinessDao,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao,
    private val sharedPreferences: SharedPreferences
    ) : IUserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    override fun checkIfUserIsLogged() = auth.currentUser != null

    override fun getCurrentUID() = auth.currentUser?.uid.toString()

    override fun getCurrentUser() = auth.currentUser

    override suspend fun syncAccountFromDatabase(userData: UserData?): SyncAccount = withContext(Dispatchers.IO){
        try {
            val userBusinesses =
                firestore.collectionGroup("employees")
                    .whereEqualTo("employeeId", getCurrentUID())
                    .whereNotEqualTo("businessStatus", "DELETED")
                    .get().await()

            if(userData != null){
                firestore
                    .collection(USERS_COLLECTION)
                    .document(getCurrentUID())
                    .update(
                        "name", userData.name,
                        "country", userData.country
                    ).await()
            }
            if(userBusinesses.isEmpty){
                businessDao.deleteAll()
                sharedPreferences.apply {
                    setShowNewUserScreenPref(true)
                    setShowLoginScreen(true)
                }
                SyncAccount.Success.BusinessNotFound
            }else{
                val businesses = userBusinesses.toObjects(Business::class.java)
                businessDao.syncEmployees(businesses)

                businesses.filter { it.type == LOCAL }
                    .map { Statistic(businessId = it.businessId) }
                    .let { if(it.isNotEmpty()) statisticsDao.insertAccountStatistic(it) }

                businesses.first().let {
                    usersDao.updateUserCurrentBusiness(it.businessId)
                }

                sharedPreferences.apply {
                    setShowNewUserScreenPref(false)
                    setShowLoginScreen(true)
                }
                SyncAccount.Success.HasBusiness
            }
        }catch (e:Exception){
            SyncAccount.Error(e)
        }
    }

    override suspend fun getUserBusiness() = businessDao.getBusiness()

    override suspend fun sendReportToFirestore(message: String): SimpleResult {
        val report = hashMapOf(
            USER_ID_FIELD to getCurrentUID(),
            REPORT_FIELD_FIRESTORE to message,
            TIMESTAMP_FIELD_FIRESTORE to Timestamp.now()
        )
        return try {
            firestore.collection(SUGGESTION_COLLECTION_NAME).document().set(report).await()
            SimpleResult.Success
        }catch (e: Exception){
            SimpleResult.Failure
        }
    }
}