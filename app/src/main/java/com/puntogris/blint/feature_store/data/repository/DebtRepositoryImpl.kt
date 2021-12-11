package com.puntogris.blint.feature_store.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.feature_store.data.data_source.local.dao.DebtsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.UsersDao
import com.puntogris.blint.feature_store.domain.model.order.Debt
import com.puntogris.blint.feature_store.domain.repository.DebtRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DebtRepositoryImpl(
    private val debtsDao: DebtsDao,
    private val usersDao: UsersDao,
    private val dispatcher: DispatcherProvider
) : DebtRepository {

    override suspend fun saveDebt(debt: Debt): SimpleResource =
        withContext(dispatcher.io) {
            SimpleResource.build {
                debt.storeId = usersDao.getCurrentStoreId()

                debtsDao.updateTraderDebt(debt.traderId, debt.amount)
                debtsDao.updateTotalTradersDebt(debt.amount)
                debtsDao.insert(debt)
            }
        }

    override fun getLastTraderDebts(traderId: String): Flow<PagingData<Debt>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) { debtsDao.getTraderDebtsPaged(traderId) }.flow
    }

    override fun getDebtsPaged(): Flow<PagingData<Debt>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) { debtsDao.getDebtsPaged() }.flow
    }
}