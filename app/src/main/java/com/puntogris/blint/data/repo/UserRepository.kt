package com.puntogris.blint.data.repo

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.EmployeeDao
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.repo.irepo.IUserRepository
import com.puntogris.blint.model.*
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.BUG_REPORT_COLLECTION_NAME
import com.puntogris.blint.utils.Constants.LOCAL
import com.puntogris.blint.utils.Constants.REPORT_FIELD_FIRESTORE
import com.puntogris.blint.utils.Constants.TIMESTAMP_FIELD_FIRESTORE
import com.puntogris.blint.utils.Constants.USERS_COLLECTION
import com.puntogris.blint.utils.Util.isTimeStampExpired
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val employeeDao: EmployeeDao,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao,
    private val sharedPref: SharedPref
    ) :IUserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    override fun checkIfUserIsLogged() = auth.currentUser != null

    override fun getCurrentUID() = auth.currentUser?.uid.toString()

    override fun getCurrentUser() = auth.currentUser

    override fun getOwnerBusiness(): StateFlow<RepoResult<List<Employee>>> =
        MutableStateFlow<RepoResult<List<Employee>>>(RepoResult.InProgress).also { result ->
            firestore.collectionGroup("business").whereEqualTo("owner", getCurrentUID()).get()
                .addOnSuccessListener { snap ->
                    if (!snap.documents.isNullOrEmpty()) {
                        result.value = RepoResult.Success(snap.toObjects(Employee::class.java))
                    }
                }
                .addOnFailureListener { result.value = RepoResult.Error(it) }
            }

    override fun getBusinessEmployees(businessId:String): StateFlow<UserBusiness>  =
    MutableStateFlow<UserBusiness>(UserBusiness.InProgress).also { result->
        firestore.collectionGroup("employees").whereEqualTo("businessId", businessId).get()
            .addOnSuccessListener {
                if (!it.documents.isNullOrEmpty()){
                    result.value = UserBusiness.Success(it.toObjects(Employee::class.java))
                } else result.value = UserBusiness.NotFound
            }
            .addOnFailureListener { result.value = UserBusiness.Error(it) }
    }

   override suspend fun generateJoiningCode(businessId: String): RepoResult<JoinCode> = withContext(Dispatchers.IO){
        try {
            val lastBusinessCode =
                firestore
                .collection("joining_businesses_codes")
                .whereEqualTo("businessId", businessId)
                .whereEqualTo("ownerId", getCurrentUID())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get().await()

            if (lastBusinessCode.isEmpty || isTimeStampExpired(lastBusinessCode.first().getTimestamp("timestamp")!!)){
                val newBusinessCode = firestore.collection("joining_businesses_codes").document()
                val newJoinCode = JoinCode(newBusinessCode.id,timestamp = Timestamp.now(), businessId, getCurrentUID())
                newBusinessCode.set(newJoinCode).await()
                RepoResult.Success(newJoinCode)
            }else{
                val lastCode = lastBusinessCode.first().toObject(JoinCode::class.java)
                RepoResult.Success(lastCode)
            }
        }catch (e:Exception){ RepoResult.Error(e) }
    }

    override suspend fun createEmployeeWithCode(code:String): JoinBusiness = withContext(Dispatchers.IO){
        try {
            val joinCode = firestore.collection("joining_businesses_codes").document(code).get().await()

            if (!joinCode.exists() || isTimeStampExpired(joinCode.getTimestamp("timestamp")!!)){
                JoinBusiness.CodeInvalid
            }else if (joinCode.get("ownerId").toString() == getCurrentUID()){
                JoinBusiness.AlreadyJoined
            }else{
                val employee = Employee(
                    employeeId = getCurrentUID(),
                    businessId = joinCode.get("businessId").toString(),
                    businessName = joinCode.get("businessName").toString(),
                    email = getCurrentUser()?.email.toString(),
                    employeeCreatedAt = Timestamp.now(),
                    businessOwner = joinCode.get("ownerId").toString(),
                    businessType = "ONLINE",
                    role = "EMPLOYEE",
                    name = usersDao.getUser().username,
                    businessCreatedAt = Timestamp.now()
                )

                firestore
                    .collection(USERS_COLLECTION)
                    .document(joinCode.get("ownerId").toString())
                    .collection("business")
                    .document(joinCode.get("businessId").toString())
                    .collection("employees")
                    .document()
                    .set(employee)
                    .await()
                employeeDao.insert(employee)
                JoinBusiness.Success
            }

        }catch (e:Exception){
            JoinBusiness.Error
        }
    }

    override suspend fun syncAccountFromDatabase(userData: UserData?): SyncAccount = withContext(Dispatchers.IO){
        try {
            val userBusinesses =
                firestore.collectionGroup("employees")
                    .whereEqualTo("employeeId", getCurrentUID())
                    .get().await()

            if(userData != null){
                usersDao.updateUserNameCountry(userData.name, userData.country)
                firestore
                    .collection(USERS_COLLECTION)
                    .document(getCurrentUID())
                    .update(
                        "name", userData.name,
                        "country", userData.country
                    ).await()
            }
            if(userBusinesses.isEmpty){
                sharedPref.setShowNewUserScreenPref(true)
                SyncAccount.Success.BusinessNotFound
            }else{
                val businesses = userBusinesses.toObjects(Employee::class.java)
                employeeDao.syncEmployees(businesses)

                businesses.filter { it.businessType == LOCAL }.map {
                    Statistic(businessId = it.businessId)
                }.let {
                    if(it.isNotEmpty()) statisticsDao.insertAccountStatistic(it)
                }

                businesses.first().let {
                    usersDao.updateCurrentBusiness(
                        id = it.businessId,
                        name = it.businessName,
                        type = it.businessType,
                        owner = it.businessOwner,
                        currentUid = getCurrentUID(),
                        status = it.businessStatus
                    )
                }
                sharedPref.setShowNewUserScreenPref(false)
                sharedPref.setLoginCompletedPref(true)
                SyncAccount.Success.HasBusiness
            }
        }catch (e:Exception){
            SyncAccount.Error(e)
        }
    }

    override suspend fun sendReportToFirestore(message: String): SimpleResult {
        val report = hashMapOf(
            REPORT_FIELD_FIRESTORE to message,
            TIMESTAMP_FIELD_FIRESTORE to Timestamp.now()
        )
        return try {
            firestore.collection(BUG_REPORT_COLLECTION_NAME).document().set(report).await()
            SimpleResult.Success
        }catch (e: Exception){
            SimpleResult.Failure
        }
    }

     override fun getEmployeeBusiness(): StateFlow<UserBusiness>  =
        MutableStateFlow<UserBusiness>(UserBusiness.InProgress).also { result->
            firestore.collectionGroup("employees").whereEqualTo("employeeId", getCurrentUID()).get()
                .addOnSuccessListener {
                    if (!it.documents.isNullOrEmpty()){
                        result.value = UserBusiness.Success(it.toObjects(Employee::class.java))
                    } else result.value = UserBusiness.NotFound
                }
                .addOnFailureListener {
                    result.value = UserBusiness.Error(it)
                }
        }
}