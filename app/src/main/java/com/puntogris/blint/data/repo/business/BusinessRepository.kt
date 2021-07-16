package com.puntogris.blint.data.repo.business

import com.google.firebase.auth.FirebaseAuth
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
import com.puntogris.blint.utils.Constants.BUSINESS_COLLECTION
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

            val refId = ref.document().id

            val business = Business(
                businessId = ref.id,
                businessName = businessName,
                type = Constants.LOCAL,
                owner = employeeId
            )
            val employee = Employee(
                businessId = ref.id,
                businessName = businessName,
                businessType = Constants.LOCAL,
                businessOwner = employeeId,
                name = user.username,
                role = Constants.ADMINISTRATOR,
                employeeId = employeeId,
                email = getCurrentUser()?.email.toString()
            )

            firestore.runBatch {
                it.set(ref.document(refId), business)
                it.set(ref.document(refId).collection("employees").document(), employee)
            }.await()

            employeeDao.insert(employee)
            usersDao.updateCurrentBusiness(business.businessId, businessName, business.type, business.owner, getCurrentUser()?.uid.toString())
            statisticsDao.insert(Statistic(businessId = refId))
            sharedPref.setShowNewUserScreenPref(false)
            SimpleResult.Success
        }catch (e:Exception){
            SimpleResult.Failure
        }
    }

    override suspend fun deleteBusinessDatabase(businessId: String): DeleteBusiness = withContext(Dispatchers.IO){
        try {
            val user = currentBusiness()
            if (user.currentBusinessIsOnline()){
                DeleteBusiness.Failure
            }else{
                employeeDao.deleteBusiness(businessId)
                val businessRemaining = employeeDao.getEmployeesList()
                if (businessRemaining.isNotEmpty()){
                    businessRemaining.first().let {
                        usersDao.updateCurrentBusiness(
                            it.businessId,
                            it.businessName,
                            it.businessType,
                            it.businessOwner,
                            getCurrentUser()?.uid.toString()
                        )
                    }
                    DeleteBusiness.Success.HasBusiness
                }else{
                    sharedPref.setShowNewUserScreenPref(false)
                    DeleteBusiness.Success.NoBusiness
                }
            }
        }catch (e:Exception){
            DeleteBusiness.Failure
        }
    }
}