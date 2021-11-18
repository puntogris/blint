package com.puntogris.blint.data.repository.debts

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.data_source.local.dao.DebtsDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.model.BusinessDebts
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Debt
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.RepoResult
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DebtsRepository @Inject constructor(
    private val debtsDao: DebtsDao,
    private val usersDao: UsersDao,
    private val dispatcher: DispatcherProvider
) : IDebtsRepository {

    override suspend fun getLastTraderDebts(traderId: Int): RepoResult<List<Debt>> {
        return try {
            val data = debtsDao.getDebtsWithId(traderId)
            RepoResult.Success(data)
        } catch (e: Exception) {
            RepoResult.Error(e)
        }
    }

    override suspend fun registerNewDebtDatabase(debt: Debt): SimpleResult =
        withContext(dispatcher.io) {

            try {
//                debt.author = ""
//                debt.businessId = user.businessId
//
//                if (debt.type == "CLIENT") {
//                    debtsDao.updateClientDebt(debt.traderId, debt.amount)
//                    debtsDao.updateClientsDebt(debt.amount)
//                } else {
//                    debtsDao.updateSupplierDebt(debt.traderId, debt.amount)
//                    debtsDao.updateSupplierDebt(debt.amount)
//                }
//                debtsDao.insert(debt)

                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override suspend fun getBusinessDebtData(): BusinessDebts = withContext(dispatcher.io) {
        debtsDao.getDebtsForBusiness()
    }

    override fun getBusinessDebtsPagingDataFlow(): Flow<PagingData<Debt>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            debtsDao.getPagedDebts()
        }.flow
    }

    override fun getClientPagingDataFlow(): Flow<PagingData<Client>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            debtsDao.getClientDebtsPaged()
        }.flow
    }

    override fun getSupplierPagingDataFlow(): Flow<PagingData<Supplier>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            debtsDao.getSupplierDebtsPaged()
        }.flow
    }
}