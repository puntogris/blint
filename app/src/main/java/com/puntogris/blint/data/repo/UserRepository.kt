package com.puntogris.blint.data.repo

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.EmployeeDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.repo.irepo.IUserRepository
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.BUG_REPORT_COLLECTION_NAME
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

class UserRepository @Inject constructor(private val employeeDao: EmployeeDao, private val usersDao: UsersDao) :
    IUserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    override fun checkIfUserIsLogged() = auth.currentUser != null

    override fun singOutCurrentUser() {
        auth.signOut()
    }

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

    override suspend fun updateUserNameCountry(username: String, country: String):SimpleResult = withContext(Dispatchers.IO) {
        try {
            firestore.collection("users").document(getCurrentUID()).update("name", username,"country", country)
            SimpleResult.Success
        }catch (e:Exception){ SimpleResult.Failure }
    }

    override fun sendEmployeeRequest(request: EmployeeRequest) =
        MutableStateFlow<RequestResult>(RequestResult.InProgress).also { result ->
            firestore.collection("users").whereEqualTo("email", request.email).get()
                .addOnSuccessListener {
                    if (it.isEmpty){
                        result.value = RequestResult.NotFound
                    }else{
                        request.employeeId = it.documents.first().id
                        request.ownerId = getCurrentUID()
                        firestore.collection("employee_requests").document().set(request)
                            .addOnSuccessListener { result.value = RequestResult.Success }
                            .addOnFailureListener { result.value = RequestResult.Error }
                    }
                }
                .addOnFailureListener { result.value = RequestResult.Error }
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
                    name = "NA NAANANANA",
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

    override suspend fun registerNewBusiness(name: String):RepoResult<String> = withContext(Dispatchers.IO) {
        try {
            val user = usersDao.getUser()
            val employeeId = getCurrentUID()
            val ref = firestore.collection("users").document(employeeId).collection("business").document()
            val business = Business(
                businessId = ref.id,
                businessName = name,
                type = "LOCAL",
                owner = employeeId
                )
            val employee = Employee(
                businessId = ref.id,
                businessName = name,
                businessType = "LOCAL",
                businessOwner = employeeId,
                name = user.username,
                role = "ADMINISTRATOR",
                employeeId = employeeId,
                email = getCurrentUser()?.email.toString()
            )
            ref.set(business).await()
            ref.collection("employees").document().set(employee)
            employeeDao.insert(employee)
            usersDao.updateCurrentBusiness(business.businessId, name,business.type, business.owner, getCurrentUID())

            RepoResult.Success(ref.id)
        }catch (e:Exception){
            RepoResult.Error(e)
        }
    }

    override fun logInUserWithCredentialToken(credentialToken: String) =
        MutableStateFlow<AuthResult>(AuthResult.InProgress).also {
            val authCredential = GoogleAuthProvider.getCredential(credentialToken, null)
            auth.signInWithCredential(authCredential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        it.value = AuthResult.Success(task.result.user!!)
                    } else {
                        task.exception?.let {e ->
                            it.value = AuthResult.Error(e)
                        }
                    }
                }
    }

    override suspend fun checkUserDataInFirestore(user: FirestoreUser): RegistrationData = withContext(Dispatchers.IO){
        try {
            val document = firestore.collection(USERS_COLLECTION).document(user.uid).get().await()
            val username = document.get("name").toString()
            val country = document.get("country").toString()
            //new user
            if (!document.exists()){
                firestore.collection(USERS_COLLECTION).document(user.uid).set(user).await()
                RegistrationData.NotFound
            }//user created but no name or country data, uncompleted registration
            else if (username.isBlank() || country.isBlank()){
                    RegistrationData.Incomplete
            }else{
                //user fully registered
                RegistrationData.Complete(username, country)
            }
        }
        catch (e:Exception){
            RegistrationData.Error
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