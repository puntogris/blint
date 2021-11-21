package com.puntogris.blint.data.repository.business

import com.puntogris.blint.data.data_source.local.SharedPreferences
import com.puntogris.blint.data.data_source.local.dao.BusinessDao
import com.puntogris.blint.data.data_source.local.dao.StatisticsDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.data.data_source.remote.FirebaseClients
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Statistic
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.DeleteBusiness
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BusinessRepository @Inject constructor(
    private val businessDao: BusinessDao,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao,
    private val sharedPreferences: SharedPreferences,
    private val dispatcher: DispatcherProvider,
    private val firebaseClients: FirebaseClients
) : IBusinessRepository {

    override suspend fun registerBusiness(businessName: String): SimpleResult =
        withContext(dispatcher.io) {
            try {
                val business = Business(ownerUid = firebaseClients.currentUser?.uid ?: "")

                businessDao.insert(business)

                statisticsDao.insert(Statistic(businessId = business.businessId))
                usersDao.updateCurrentBusiness(business.businessId)

                sharedPreferences.setShowNewUserScreenPref(false)

                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override suspend fun deleteBusiness(businessId: String): DeleteBusiness =
        withContext(dispatcher.io) {
            try {
                businessDao.deleteBusiness(businessId)
                val business = businessDao.getBusiness()

                if (business.isEmpty()) DeleteBusiness.Success.NoBusiness
                else DeleteBusiness.Success.HasBusiness
            } catch (e: Exception) {
                DeleteBusiness.Failure
            }
        }
}