package com.puntogris.blint.data.repository.debts

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.data_source.local.dao.DebtsDao
import com.puntogris.blint.data.data_source.local.dao.UsersDao
import com.puntogris.blint.model.BusinessDebtsData
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Debt
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DebtsRepository @Inject constructor(
    private val debtsDao: DebtsDao,
    private val usersDao: UsersDao
) : IDebtsRepository {

    private suspend fun currentUser() = usersDao.getCurrentBusinessFromUser()

    override suspend fun getLastTraderDebts(traderId: Int): RepoResult<List<Debt>> {
        return try {
            val data = debtsDao.getDebtsWithId(traderId)
            RepoResult.Success(data)
        } catch (e: Exception) {
            RepoResult.Error(e)
        }
    }

    override suspend fun registerNewDebtDatabase(debt: Debt): SimpleResult =
        withContext(Dispatchers.IO) {
            val user = currentUser()
            try {
                debt.author = ""
                debt.businessId = user.businessId

                if (debt.type == "CLIENT") {
                    debtsDao.updateClientDebt(debt.traderId, debt.amount)
                    debtsDao.updateClientsDebt(debt.amount)
                } else {
                    debtsDao.updateSupplierDebt(debt.traderId, debt.amount)
                    debtsDao.updateSupplierDebt(debt.amount)
                }
                debtsDao.insert(debt)

                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }

    override suspend fun getBusinessDebtData(): BusinessDebtsData = withContext(Dispatchers.IO) {
        debtsDao.getDebtsForBusiness()
    }

    override suspend fun getBusinessDebtsPagingDataFlow(): Flow<PagingData<Debt>> =
        withContext(Dispatchers.IO) {
            Pager(
                PagingConfig(
                    pageSize = 30,
                    enablePlaceholders = true,
                    maxSize = 200
                )
            ) {
                debtsDao.getPagedDebts()
            }.flow
        }

    override suspend fun getClientPagingDataFlow(): Flow<PagingData<Client>> =
        withContext(Dispatchers.IO) {
            Pager(
                PagingConfig(
                    pageSize = 30,
                    enablePlaceholders = true,
                    maxSize = 200
                )
            ) {
                debtsDao.getClientDebtsPaged()
            }.flow
        }

    override suspend fun getSupplierPagingDataFlow(): Flow<PagingData<Supplier>> =
        withContext(Dispatchers.IO) {
            Pager(
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