package com.puntogris.blint.data.repo.business

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.EmployeeDao
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.JoinCode
import com.puntogris.blint.model.Statistic
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.utils.Constants.ADMINISTRATOR
import com.puntogris.blint.utils.Constants.BUSINESS_COLLECTION
import com.puntogris.blint.utils.Constants.DELETED
import com.puntogris.blint.utils.Constants.ENABLED
import com.puntogris.blint.utils.Constants.LOCAL
import com.puntogris.blint.utils.Constants.ONLINE
import com.puntogris.blint.utils.Constants.TO_DELETE
import com.puntogris.blint.utils.Constants.USERS_COLLECTION
import com.puntogris.blint.utils.DeleteBusiness
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.is10MinutesOld
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BusinessRepository @Inject constructor(
    private val employeeDao: EmployeeDao,
    private val usersDao: UsersDao,
    private val sharedPref: SharedPref,
    private val statisticsDao: StatisticsDao
):IBusinessRepository {

    val firestore = Firebase.firestore
    val auth = FirebaseAuth.getInstance()

    private suspend fun currentBusiness() = usersDao.getCurrentBusinessFromUser()

    private fun getCurrentUser() = auth.currentUser

    override suspend fun registerLocalBusiness(businessName: String): SimpleResult = withContext(Dispatchers.IO) {
        try {
            val employeeId = getCurrentUser()?.uid.toString()
            val ref = firestore
                .collection(USERS_COLLECTION)
                .document(employeeId).collection(BUSINESS_COLLECTION)
            val userRef = firestore.collection(USERS_COLLECTION).document(auth.currentUser?.uid.toString())

            val refId = ref.document().id

            val business = Business(
                businessId = refId,
                businessName = businessName,
                type = LOCAL,
                owner = employeeId,
                status = ENABLED
            )
            val employee = Employee(
                businessId = refId,
                businessName = businessName,
                businessType = LOCAL,
                businessOwner = employeeId,
                role = ADMINISTRATOR,
                employeeId = employeeId,
                email = getCurrentUser()?.email.toString(),
                businessStatus = business.status
            )

            val result =
                firestore.runTransaction { transition->
                    val data = transition.get(userRef)
                    employee.name = data.get("name").toString()
                    val counter = data.get("businessesCounter").toString().toIntOrNull() ?: 0
                    if(counter < 10){
                        transition.set(ref.document(refId), business)
                        transition.update(userRef,"businessesCounter",FieldValue.increment(1))
                        transition.set(ref.document(refId).collection("employees").document(employeeId), employee)
                        SimpleResult.Success
                    }else SimpleResult.Failure
            }.await()

            if (result is SimpleResult.Success){
                employeeDao.insert(employee)
                usersDao.updateUserCurrentBusiness(business.businessId)
                statisticsDao.insert(Statistic(businessId = refId))
                sharedPref.setShowNewUserScreenPref(false)
            }

            result
        }catch (e:Exception){
            SimpleResult.Failure
        }
    }

    override suspend fun deleteBusinessDatabase(businessId: String): DeleteBusiness = withContext(Dispatchers.IO){
        try {
            val user = currentBusiness()
            val businessRemaining = employeeDao.getEmployeesList()

            val business = businessRemaining.singleOrNull{ it.businessId == businessId}

            if (business != null && business.businessOwner == getCurrentUser()?.uid.toString()){
                if (business.businessType == ONLINE){
                    firestore.runTransaction {
                        val businessRef =
                            firestore.collection(USERS_COLLECTION).document(user.businessOwner)
                                .collection("business").document(businessId)
                        val businessFirestore = it.get(businessRef).toObject(Business::class.java)
                        if (businessFirestore != null && businessFirestore.status != TO_DELETE){
                            it.update(businessRef,
                                "status", TO_DELETE,
                                "lastStatusTimestamp", Timestamp.now()
                                )
                            DeleteBusiness.Success.HasBusiness
                        }else DeleteBusiness.Failure
                    }.await()
                }else{
                    val businessRef = firestore
                        .collection(USERS_COLLECTION)
                        .document(user.businessOwner)
                        .collection("business")
                        .document(businessId)

                    val userRef = firestore
                        .collection(USERS_COLLECTION)
                        .document(auth.currentUser?.uid.toString())

                    firestore.runBatch {
                        it.update(businessRef, "status", DELETED,
                            "lastStatusTimestamp", Timestamp.now())
                        it.update(userRef,"businessesCounter", FieldValue.increment(-1))
                    }

                    employeeDao.deleteEmployeeWithBusinessId(businessId)
                    if (businessRemaining.isNotEmpty()){
                        businessRemaining.first().let {
                            usersDao.updateUserCurrentBusiness(it.businessId)
                        }
                        DeleteBusiness.Success.HasBusiness
                    }else{
                        sharedPref.setShowNewUserScreenPref(true)
                        DeleteBusiness.Success.NoBusiness
                    }
                }
            } else DeleteBusiness.Failure
        }catch (e:Exception){
            DeleteBusiness.Failure
        }
    }

    override suspend fun generateJoiningCode(businessId: String): RepoResult<JoinCode> = withContext(Dispatchers.IO){
        try {
            val uid = getCurrentUser()?.uid.toString()
            val codeRef =
                firestore
                    .collection(USERS_COLLECTION)
                    .document(uid)
                    .collection("business")
                    .document(businessId)
                    .collection("join_codes")

            val lastBusinessCode = codeRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get().await()

            if (lastBusinessCode.isEmpty || lastBusinessCode.first().getTimestamp("timestamp")!!.is10MinutesOld()){
                val newBusinessCode = codeRef.document()
                val newJoinCode = JoinCode(newBusinessCode.id, timestamp = Timestamp.now(), businessId, uid)
                newBusinessCode.set(newJoinCode).await()
                RepoResult.Success(newJoinCode)
            }else{
                val lastCode = lastBusinessCode.first().toObject(JoinCode::class.java)
                RepoResult.Success(lastCode)
            }
        }catch (e:Exception){ RepoResult.Error(e) }
    }

}