package com.puntogris.blint.feature_store.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.DispatcherProvider
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.data.data_source.local.dao.DebtsDao
import com.puntogris.blint.feature_store.data.data_source.local.dao.UsersDao
import com.puntogris.blint.feature_store.domain.model.order.Debt
import com.puntogris.blint.feature_store.domain.repository.DebtsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DebtsRepositoryImpl(
    private val debtsDao: DebtsDao,
    private val usersDao: UsersDao,
    private val dispatcher: DispatcherProvider
) : DebtsRepository {

    override suspend fun saveDebt(debt: Debt): SimpleResult =
        withContext(dispatcher.io) {
            try {
                debt.businessId = usersDao.getCurrentBusinessId()

                if (debt.traderType == Constants.CLIENT) {
                    debtsDao.updateClientDebt(debt.traderId, debt.amount)
                    debtsDao.updateTotalClientsDebt(debt.amount)
                } else {
                    debtsDao.updateTotalSupplierDebt(debt.traderId, debt.amount)
                    debtsDao.updateTotalSupplierDebt(debt.amount)
                }
                debtsDao.insert(debt)

                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
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