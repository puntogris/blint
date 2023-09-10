package com.puntogris.blint.feature_store.data.repository

import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.types.DeleteStore
import com.puntogris.blint.common.utils.types.ProgressResource
import com.puntogris.blint.common.utils.types.SimpleProgressResource
import com.puntogris.blint.feature_store.data.data_source.local.SharedPreferences
import com.puntogris.blint.feature_store.data.data_source.local.dao.StoreDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.UsersDao
import com.puntogris.blint.feature_store.domain.model.Store
import com.puntogris.blint.feature_store.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class StoreRepositoryImpl(
    private val storeDao: StoreDao,
    private val usersDao: UsersDao,
    private val sharedPreferences: SharedPreferences,
    private val dispatcher: DispatcherProvider,
) : StoreRepository {

    override fun registerStore(businessName: String): Flow<SimpleProgressResource> = flow {
        try {
            emit(ProgressResource.InProgress)

            val user = usersDao.getUser()

            val business = Store(
                name = businessName,
                ownerUid = user.uid,
                author = user.email
            )

            storeDao.insert(business)
            usersDao.updateCurrentStore(business.storeId)
            sharedPreferences.setShowNewUserScreen(false)

            emit(ProgressResource.Success(Unit))
        } catch (e: Exception) {
            emit(ProgressResource.Error())
        }
    }.flowOn(dispatcher.io)

    override suspend fun deleteStore(businessId: String): DeleteStore =
        withContext(dispatcher.io) {
            try {
                storeDao.deleteStore(businessId)
                val business = storeDao.getStores()

                if (business.isEmpty()) {
                    sharedPreferences.setShowNewUserScreen(true)
                    DeleteStore.Success.NoStores
                } else {
                    usersDao.updateCurrentStore(business.first().storeId)
                    DeleteStore.Success.HasStores
                }
            } catch (e: Exception) {
                DeleteStore.Failure
            }
        }

    override fun getStoresFlow() = storeDao.getStoresFlow()

    override fun getCurrentStoreFlow() = storeDao.getCurrentStoreFlow()
}
