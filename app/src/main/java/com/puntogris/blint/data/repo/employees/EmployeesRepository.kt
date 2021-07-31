package com.puntogris.blint.data.repo.employees

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.data.local.dao.EmployeeDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.model.JoinCode
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.BUSINESS_COLLECTION
import com.puntogris.blint.utils.Constants.USERS_COLLECTION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EmployeesRepository @Inject constructor(
    private val employeeDao: EmployeeDao,
    private val sharedPref: SharedPref,
    private val usersDao: UsersDao
):IEmployeesRepository {

    private val firestore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private fun getCurrentUserId() = auth.currentUser?.uid.toString()
    private fun getCurrentUser() = auth.currentUser

    override suspend fun getEmployeeListRoom() = employeeDao.getEmployeesList()

    override suspend fun deleteEmployeeFromBusiness(employee: Employee): SimpleResult = withContext(
        Dispatchers.IO){
        try {
            val userData = employeeDao.getEmployeeWithBusinessId(employee.businessId)
            if (
                userData.businessOwner == getCurrentUserId() &&
                userData.isBusinessOnline()
            ){
                firestore
                    .collection(USERS_COLLECTION)
                    .document(employee.businessOwner)
                    .collection(BUSINESS_COLLECTION)
                    .document(employee.businessId)
                    .collection("employees")
                    .document(employee.employeeId)
                    .delete().await()

                SimpleResult.Success
            }else SimpleResult.Failure
        }catch (e:Exception){
            SimpleResult.Failure
        }
    }

    override suspend fun createEmployeeWithCode(code:String): JoinBusiness = withContext(Dispatchers.IO){
        try {
            val joinCodes = firestore
                .collectionGroup("join_codes")
                .whereEqualTo("codeId", code)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get().await()

            if (joinCodes.isEmpty){
                JoinBusiness.CodeInvalid
            }else{
                val joinCode = joinCodes.first().toObject(JoinCode::class.java)
                if (joinCode.ownerId == getCurrentUserId() ||
                    joinCode.businessId in employeeDao.getBusinessIdsList()){
                        JoinBusiness.AlreadyJoined
                }else if(joinCode.timestamp.is10MinutesOld()){
                    JoinBusiness.CodeInvalid
                }else{
                    val employee = firestore.runTransaction {
                        val userRef = firestore.collection(USERS_COLLECTION).document(getCurrentUserId())

                        val businessRef = firestore.collection(USERS_COLLECTION)
                            .document(joinCode.ownerId)
                            .collection(BUSINESS_COLLECTION)
                            .document(joinCode.businessId)

                        val user = it.get(userRef).toObject(FirestoreUser::class.java)!!
                        val business = it.get(businessRef).toObject(Business::class.java)

                        if (business != null){
                            val employee = Employee(
                                employeeId = getCurrentUserId(),
                                businessId = business.businessId,
                                businessName = business.businessName,
                                email = getCurrentUser()?.email.toString(),
                                employeeCreatedAt = Timestamp.now(),
                                businessOwner = business.owner,
                                businessType = business.type,
                                role = "EMPLOYEE",
                                name = user.name,
                                businessCreatedAt = business.businessCreatedAt,
                                businessStatus = business.status
                            )

                            val employeeRef =
                                firestore
                                    .collection(USERS_COLLECTION)
                                    .document(joinCode.ownerId)
                                    .collection(BUSINESS_COLLECTION)
                                    .document(joinCode.businessId)
                                    .collection("employees")
                                    .document(employee.employeeId)

                            it.set(employeeRef, employee)
                            employee
                        }else null
                    }.await()

                    if (employee == null) JoinBusiness.Error
                    else{
                        sharedPref.setShowNewUserScreenPref(false)
                        employeeDao.insert(employee)
                        usersDao.updateUserCurrentBusiness(employee.businessId)
                        JoinBusiness.Success
                    }
                }
            }
        }catch (e:Exception){ JoinBusiness.Error }
    }

    override suspend fun getEmployeeWithBusinessId(businessId: String) = employeeDao.getEmployeeWithBusinessId(businessId)

    override fun getBusinessEmployees(businessId:String): StateFlow<UserBusiness> =
        MutableStateFlow<UserBusiness>(UserBusiness.InProgress).also { result->
            firestore.collectionGroup("employees").whereEqualTo("businessId", businessId).get()
                .addOnSuccessListener {
                    if (!it.documents.isNullOrEmpty()){
                        result.value = UserBusiness.Success(it.toObjects(Employee::class.java))
                    } else result.value = UserBusiness.NotFound
                }
                .addOnFailureListener { result.value = UserBusiness.Error(it) }
        }

}