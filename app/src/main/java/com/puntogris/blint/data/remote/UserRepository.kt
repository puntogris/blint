package com.puntogris.blint.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.businesses.BusinessDao
import com.puntogris.blint.data.local.user.UsersDao
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.utils.AuthResult
import com.puntogris.blint.utils.Constants.BUG_REPORT_COLLECTION_NAME
import com.puntogris.blint.utils.Constants.REPORT_FIELD_FIRESTORE
import com.puntogris.blint.utils.Constants.TIMESTAMP_FIELD_FIRESTORE
import com.puntogris.blint.utils.Constants.USERS_COLLECTION
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class UserRepository @Inject constructor(private val usersDao: UsersDao, private val businessDao: BusinessDao) : IUserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = Firebase.firestore

    override fun checkIfUserIsLogged() = auth.currentUser != null

    override fun singOutCurrentUser() {
        auth.signOut()
    }

    override fun getCurrentUID() = auth.currentUser?.uid.toString()

    override fun getCurrentUser() = auth.currentUser

    override fun getOwnerBusiness(): StateFlow<RepoResult<List<Business>>> =
        MutableStateFlow<RepoResult<List<Business>>>(RepoResult.InProgress).also { result ->
            firestore.collectionGroup("business").whereEqualTo("owner", getCurrentUID()).get()
                .addOnSuccessListener { snap ->
                    if (!snap?.documents.isNullOrEmpty()) {
                        val data = snap!!.documents.map { doc ->
                            Business(
                                name = doc["name"].toString(),
                                id = doc["id"].toString(),
                                userRole = doc["userRole"].toString(),
                                type = doc["type"].toString(),
                                owner = doc["owner"].toString(),
                                userID = doc["userID"].toString()
                            )
                        }
                        result.value = RepoResult.Success(data)
                    }
                }
                .addOnFailureListener {
                    result.value = RepoResult.Error(it)
                }
            }


    override suspend fun registerNewBusiness(name: String) = withContext(Dispatchers.IO) {
        try {
            val ref = firestore.collection("users").document(getCurrentUID()).collection("business").document()
            val business = Business(
                id = ref.id,
                name = name,
                type = "LOCAL",
                owner = getCurrentUID(),
                userRole = "ADMINISTRATOR"
            )
            ref.set(business).await()
            business.userID = getCurrentUID()
            ref.collection("employees").document().set(business)
            businessDao.insert(business)

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

     override fun getEmployeeBusiness(): StateFlow<List<Business>>  =
        MutableStateFlow(listOf<Business>()).also {
            firestore.collectionGroup("employees").whereEqualTo("userID", getCurrentUID()).addSnapshotListener { snap, _ ->
                if (!snap?.documents.isNullOrEmpty()){
                    val data = snap!!.documents.map { doc->
                        Business(
                            name = doc["name"].toString(),
                            id = doc["id"].toString(),
                            userRole = doc["userRole"].toString(),
                            type = doc["type"].toString(),
                            owner = doc["owner"].toString(),
                            userID = doc["userID"].toString()
                        )
                    }
                    it.value = data
                }
                else it.value = emptyList()
            }
        }
}