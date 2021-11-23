package com.puntogris.blint.data.repository.user

import com.puntogris.blint.data.data_source.local.SharedPreferences
import com.puntogris.blint.data.data_source.local.dao.BusinessDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.data.data_source.remote.UserServerApi
import com.puntogris.blint.model.AuthUser
import com.puntogris.blint.model.User
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.SyncAccount
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val businessDao: BusinessDao,
    private val usersDao: UsersDao,
    private val sharedPreferences: SharedPreferences,
    private val dispatcher: DispatcherProvider,
    private val userServerApi: UserServerApi
) : IUserRepository {

    override suspend fun syncUserAccount(authUser: AuthUser?): SyncAccount =
        withContext(dispatcher.io) {
            try {
                val user = if (authUser != null) userServerApi.getUserAccount(authUser) else User()
                usersDao.insert(user)

                val business = businessDao.getBusiness()

                if (business.isNotEmpty()) {
                    businessDao.updateBusinessesOwnerUid(user.uid)
                    usersDao.updateCurrentBusiness(business.first().businessId)
                    sharedPreferences.setShowNewUserScreenPref(true)
                    SyncAccount.Success.HasBusiness
                } else {
                    sharedPreferences.setShowNewUserScreenPref(false)
                    SyncAccount.Success.BusinessNotFound
                }.also {
                    sharedPreferences.setShowLoginScreen(false)
                }

            } catch (e: Exception) {
                SyncAccount.Error(e)
            }
        }

    override fun getUserFlow() = usersDao.getUserFlow()

}