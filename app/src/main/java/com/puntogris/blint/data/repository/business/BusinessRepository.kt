package com.puntogris.blint.data.repository.business

import com.google.firebase.auth.FirebaseAuth
import com.puntogris.blint.data.data_source.local.SharedPreferences
import com.puntogris.blint.data.data_source.local.dao.BusinessDao
import com.puntogris.blint.data.data_source.local.dao.StatisticsDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.DeleteBusiness
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BusinessRepository @Inject constructor(
    private val businessDao: BusinessDao,
    private val usersDao: UsersDao,
    private val sharedPreferences: SharedPreferences,
    private val statisticsDao: StatisticsDao,
    private val dispatcher: DispatcherProvider
) : IBusinessRepository {

    val auth = FirebaseAuth.getInstance()

    private suspend fun currentBusiness() = usersDao.getCurrentBusinessFromUser()

    private fun getCurrentUser() = auth.currentUser

    override suspend fun registerLocalBusiness(businessName: String): SimpleResult =
        withContext(dispatcher.io) {
            try {
                val employeeId = getCurrentUser()?.uid.toString()


//                val business = Business(
//                    businessId = refId,
//                    businessName = businessName,
//                    type = LOCAL,
//                    owner = employeeId,
//                    status = ENABLED
//                )
//                val employee = Employee(
//                    businessId = refId,
//                    businessName = businessName,
//                    businessType = LOCAL,
//                    businessOwner = employeeId,
//                    role = ADMINISTRATOR,
//                    employeeId = employeeId,
//                    email = getCurrentUser()?.email.toString(),
//                    businessStatus = business.status
//                )
//
//                employeeDao.insert(employee)
//                usersDao.updateUserCurrentBusiness(business.businessId)
//                statisticsDao.insert(Statistic(businessId = refId))
//                sharedPref.setShowNewUserScreenPref(false)

                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override suspend fun deleteBusinessDatabase(businessId: String): DeleteBusiness =
        withContext(dispatcher.io) {
            try {
//                val user = currentBusiness()
//                val businessRemaining = businessDao.getEmployeesList()
//
//                val business = businessRemaining.singleOrNull { it.businessId == businessId }
//
//                if (business != null && business.businessOwner == getCurrentUser()?.uid.toString()) {
//
//                        businessDao.deleteEmployeeWithBusinessId(businessId)
//                        if (businessRemaining.isNotEmpty()) {
//                            businessRemaining.first().let {
//                                usersDao.updateUserCurrentBusiness(it.businessId)
//                            }
//                            DeleteBusiness.Success.HasBusiness
//                        } else {
//                            sharedPref.setShowNewUserScreenPref(true)
//                            DeleteBusiness.Success.NoBusiness
//                        }
//
//                } else
                DeleteBusiness.Failure
            } catch (e: Exception) {
                DeleteBusiness.Failure
            }
        }

}