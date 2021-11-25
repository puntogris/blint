package com.puntogris.blint.data.repository

import com.puntogris.blint.data.data_source.local.SharedPreferences
import com.puntogris.blint.data.data_source.local.dao.BusinessDao
import com.puntogris.blint.data.data_source.local.dao.StatisticsDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.domain.repository.BusinessRepository
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Statistic
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.DeleteBusiness
import com.puntogris.blint.utils.types.RepoResult
import com.puntogris.blint.utils.types.SimpleRepoResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class BusinessRepositoryImpl(
    private val businessDao: BusinessDao,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao,
    private val sharedPreferences: SharedPreferences,
    private val dispatcher: DispatcherProvider,
) : BusinessRepository {

    override fun registerBusiness(businessName: String): Flow<SimpleRepoResult> = flow {
        try {
            emit(RepoResult.InProgress)
            val business = Business(ownerUid = usersDao.getUserId())

            businessDao.insert(business)

            statisticsDao.insert(Statistic(businessId = business.businessId))
            usersDao.updateCurrentBusiness(business.businessId)

            sharedPreferences.setShowNewUserScreenPref(false)

            emit(RepoResult.Success(Unit))
        } catch (e: Exception) {
            emit(RepoResult.Error())
        }
    }.flowOn(dispatcher.io)

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

    override fun getBusinessFlow() = businessDao.getBusinessFlow()

}