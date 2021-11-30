package com.puntogris.blint.feature_store.data.repository

import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.types.DeleteBusiness
import com.puntogris.blint.common.utils.types.RepoResult
import com.puntogris.blint.common.utils.types.SimpleRepoResult
import com.puntogris.blint.feature_store.data.data_source.local.SharedPreferences
import com.puntogris.blint.feature_store.data.data_source.local.dao.BusinessDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.UsersDao
import com.puntogris.blint.feature_store.domain.model.Business
import com.puntogris.blint.feature_store.domain.repository.BusinessRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class BusinessRepositoryImpl(
    private val businessDao: BusinessDao,
    private val usersDao: UsersDao,
    private val sharedPreferences: SharedPreferences,
    private val dispatcher: DispatcherProvider,
) : BusinessRepository {

    override fun registerBusiness(businessName: String): Flow<SimpleRepoResult> = flow {
        try {
            emit(RepoResult.InProgress)
            val business = Business(
                name = businessName,
                ownerUid = usersDao.getUserId()
            )

            businessDao.insert(business)

            usersDao.updateCurrentBusiness(business.businessId)

            sharedPreferences.setShowNewUserScreen(false)

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

                if (business.isEmpty()) {
                    sharedPreferences.setShowNewUserScreen(true)
                    DeleteBusiness.Success.NoBusiness
                }
                else DeleteBusiness.Success.HasBusiness
            } catch (e: Exception) {
                DeleteBusiness.Failure
            }
        }

    override fun getBusinessesFlow() = businessDao.getBusinessesFlow()

    override fun getCurrentBusinessFlow() = businessDao.getCurrentBusinessFlow()

}