package com.puntogris.blint.data.repository.user

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.data_source.local.SharedPreferences
import com.puntogris.blint.data.data_source.local.dao.BusinessDao
import com.puntogris.blint.data.data_source.local.dao.StatisticsDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.data.data_source.remote.UserServerApi
import com.puntogris.blint.model.AuthUser
import com.puntogris.blint.model.User
import com.puntogris.blint.utils.Constants.REPORT_FIELD_FIRESTORE
import com.puntogris.blint.utils.Constants.SUGGESTION_COLLECTION_NAME
import com.puntogris.blint.utils.Constants.TIMESTAMP_FIELD_FIRESTORE
import com.puntogris.blint.utils.Constants.USER_ID_FIELD
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.SimpleResult
import com.puntogris.blint.utils.types.SyncAccount
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val businessDao: BusinessDao,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao,
    private val sharedPreferences: SharedPreferences,
    private val dispatcher: DispatcherProvider,
    private val userServerApi: UserServerApi
) : IUserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    override fun checkIfUserIsLogged() = auth.currentUser != null

    override fun getCurrentUID() = auth.currentUser?.uid.toString()

    override fun getCurrentUser() = auth.currentUser

    override suspend fun syncUserAccount(authUser: AuthUser?): SyncAccount =
        withContext(dispatcher.io) {
            try {
                val user = if (authUser != null) userServerApi.getUserAccount(authUser) else User()
                usersDao.insert(user)

                val business = businessDao.getBusiness()

                if (business.isNotEmpty()) {
                    businessDao.updateBusinessOwnerUid(user.uid)
                    usersDao.updateUserCurrentBusiness(business.first().businessId)

                    sharedPreferences.setShowLoginScreen(false)
                    SyncAccount.Success.BusinessNotFound
                } else {
                    sharedPreferences.setShowLoginScreen(false)
                    SyncAccount.Success.HasBusiness
                }
            } catch (e: Exception) {
                SyncAccount.Error(e)
            }
        }

    override suspend fun getUserBusiness() = businessDao.getBusiness()

    override suspend fun sendReportToFirestore(message: String): SimpleResult =
        withContext(dispatcher.io) {
            SimpleResult.build {
                val report = hashMapOf(
                    USER_ID_FIELD to getCurrentUID(),
                    REPORT_FIELD_FIRESTORE to message,
                    TIMESTAMP_FIELD_FIRESTORE to Timestamp.now()
                )

                firestore.collection(SUGGESTION_COLLECTION_NAME).document().set(report).await()
            }
        }
}