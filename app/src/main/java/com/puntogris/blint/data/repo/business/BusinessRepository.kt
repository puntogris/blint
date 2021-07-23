package com.puntogris.blint.data.repo.business

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.EmployeeDao
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.Statistic
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.utils.Constants
import com.puntogris.blint.utils.Constants.ADMINISTRATOR
import com.puntogris.blint.utils.Constants.BUSINESS_COLLECTION
import com.puntogris.blint.utils.Constants.DELETED
import com.puntogris.blint.utils.Constants.ENABLED
import com.puntogris.blint.utils.Constants.LOCAL
import com.puntogris.blint.utils.Constants.ONLINE
import com.puntogris.blint.utils.Constants.TO_DELETE
import com.puntogris.blint.utils.Constants.USERS_COLLECTION
import com.puntogris.blint.utils.DeleteBusiness
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
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

    private suspend fun currentBusiness() = usersDao.getUser()

    private fun getCurrentUser() = auth.currentUser

    override suspend fun registerLocalBusiness(businessName: String): SimpleResult = withContext(Dispatchers.IO) {
        try {
            val user = usersDao.getUser()
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
                name = user.username,
                role = ADMINISTRATOR,
                employeeId = employeeId,
                email = getCurrentUser()?.email.toString(),
                businessStatus = business.status
            )

            val result =
                firestore.runTransaction { transition->
                val data = transition.get(userRef)
                val counter = data.get("businessesCounter").toString().toIntOrNull() ?: 0
                if(counter < 10){
                    transition.set(ref.document(refId), business)
                    transition.update(userRef,"businessesCounter",FieldValue.increment(1))
                    transition.set(ref.document(refId).collection("employees").document(getCurrentUser()?.uid.toString()), employee)
                    SimpleResult.Success
                }else SimpleResult.Failure
            }.await()

            if (result is SimpleResult.Success){
                employeeDao.insert(employee)
                usersDao.updateCurrentBusiness(business.businessId, businessName, business.type, business.owner, getCurrentUser()?.uid.toString(), business.status)
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
                            firestore.collection(USERS_COLLECTION).document(user.currentBusinessOwner)
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
                        .document(user.currentBusinessOwner)
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

                    employeeDao.deleteBusiness(businessId)
                    if (businessRemaining.isNotEmpty()){
                        businessRemaining.first().let {
                            usersDao.updateCurrentBusiness(
                                it.businessId,
                                it.businessName,
                                it.businessType,
                                it.businessOwner,
                                getCurrentUser()?.uid.toString(),
                                it.businessStatus
                            )
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

}