package com.puntogris.blint.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.EmployeeDao
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.utils.AuthResult
import com.puntogris.blint.utils.Constants.BUG_REPORT_COLLECTION_NAME
import com.puntogris.blint.utils.Constants.REPORT_FIELD_FIRESTORE
import com.puntogris.blint.utils.Constants.TIMESTAMP_FIELD_FIRESTORE
import com.puntogris.blint.utils.Constants.USERS_COLLECTION
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.UserBusiness
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(private val employeeDao: EmployeeDao) : IUserRepository {

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
                    if (!snap?.documents.isNullOrEmpty()) {
                        val data = snap!!.documents.map { doc ->
                            Employee(
                                businessName = doc["businessName"].toString(),
                                name = doc["name"].toString(),
                                businessId = doc["businessId"].toString(),
                                role = doc["role"].toString(),
                                businessType = doc["type"].toString(),
                                owner = doc["owner"].toString(),
                                employeeId = doc["employeeId"].toString(),
                                email = doc["email"].toString()
                            )
                        }
                        result.value = RepoResult.Success(data)
                    }
                }
                .addOnFailureListener {
                    result.value = RepoResult.Error(it)
                }
            }

    override fun getBusinessEmployees(businessId:String): StateFlow<UserBusiness>  =
    MutableStateFlow<UserBusiness>(UserBusiness.InProgress).also { result->
        firestore.collectionGroup("employees").whereEqualTo("businessId", businessId).get()
            .addOnSuccessListener {
                if (!it.documents.isNullOrEmpty()){
                    val data = it.documents.map { doc->
                        Employee(
                            businessName = doc["businessName"].toString(),
                            name = doc["name"].toString(),
                            businessId = doc["businessId"].toString(),
                            role = doc["role"].toString(),
                            businessType = doc["type"].toString(),
                            owner = doc["owner"].toString(),
                            employeeId = doc["employeeId"].toString(),
                            businessTimestamp = doc.getTimestamp("creationTimestamp")!!,
                            email = doc["email"].toString()
                        )
                    }
                    result.value = UserBusiness.Success(data)
                } else result.value = UserBusiness.NotFound
            }
            .addOnFailureListener {
                result.value = UserBusiness.Error(it)
            }

    }

    override suspend fun updateUserNameCountry(username: String, country: String):SimpleResult {
        return try {
            firestore.collection("users").document(getCurrentUID()).update("name", username,"country", country)
            SimpleResult.Success
        }catch (e:Exception){
            SimpleResult.Failure
        }
    }

    override suspend fun checkIfUserExistWithEmail(email: String): SimpleResult = withContext(Dispatchers.IO) {
        try {
            val result = firestore.collection("users").whereEqualTo("email", email).get().await()
            if (!result.isEmpty){
                SimpleResult.Success
            }else{
                SimpleResult.Failure
            }
        }
        catch (e:Exception){
            SimpleResult.Failure
        }
    }


    override suspend fun registerNewBusiness(name: String) = withContext(Dispatchers.IO) {
        try {
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
                owner = employeeId,
                role = "ADMINISTRATOR",
                employeeId = employeeId,
                email = getCurrentUser()?.email.toString()
            )
            ref.set(business).await()
            ref.collection("employees").document().set(employee)
            employeeDao.insert(employee)
        }catch (e:Exception){

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

    override suspend fun checkUserDataInFirestore(user: FirestoreUser): SimpleResult = withContext(Dispatchers.IO){
        try {
            val document = firestore.collection(USERS_COLLECTION).document(user.uid).get().await()
            if (!document.exists()){
                firestore.collection(USERS_COLLECTION).document(user.uid).set(user).await()
            }
            SimpleResult.Success
        }
        catch (e:Exception){
            SimpleResult.Failure
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
            firestore.collectionGroup("employees").whereEqualTo("userID", getCurrentUID()).get()
                .addOnSuccessListener {
                    if (!it.documents.isNullOrEmpty()){
                        val data = it.documents.map { doc->
                            Employee(
                                businessName = doc["businessName"].toString(),
                                name = doc["name"].toString(),
                                businessId = doc["businessId"].toString(),
                                role = doc["role"].toString(),
                                businessType = doc["type"].toString(),
                                owner = doc["owner"].toString(),
                                employeeId = doc["employeeId"].toString(),
                                businessTimestamp = doc.getTimestamp("creationTimestamp")!!,
                                email = doc["email"].toString()
                            )
                        }
                        result.value = UserBusiness.Success(data)
                    } else result.value = UserBusiness.NotFound
                }
                .addOnFailureListener {
                    result.value = UserBusiness.Error(it)
                }

        }
}