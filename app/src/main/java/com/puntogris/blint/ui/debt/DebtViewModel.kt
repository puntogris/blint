package com.puntogris.blint.ui.debt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.google.firebase.Timestamp
import com.puntogris.blint.data.local.dao.ClientsDao
import com.puntogris.blint.data.local.dao.DebtsDao
import com.puntogris.blint.data.local.dao.SuppliersDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Debt
import com.puntogris.blint.model.SimpleDebt
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.utils.Constants.CLIENT_DEBT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DebtViewModel @Inject constructor(
    private val debtsDao: DebtsDao,
    private val suppliersDao: SuppliersDao,
    private val clientsDao: ClientsDao,
    private val userRepository: UserRepository
    ): ViewModel() {

    private val debt = Debt()

    fun updateDebtWithTraderInfo(id: String, name: String, businessId:String){
        debt.traderId = id
        debt.traderName = name
        debt.businessId = businessId
    }

    fun getDebtData() = debt

    suspend fun registerNewDebt(debt:Debt, debtType: Int){
        debt.author = userRepository.getCurrentUser()?.email!!
        debt.timestamp = Timestamp.now()
        debt.type = if(debtType == CLIENT_DEBT) "CLIENT" else "SUPPLIER"
        debtsDao.insert(debt)

        if (debtType == CLIENT_DEBT){
            clientsDao.updateClientDebt(debt.traderId, debt.amount)
            debtsDao.updateClientsDebt(debt.amount)
        }else{
            suppliersDao.updateSupplierDebt(debt.traderId, debt.amount)
            debtsDao.updateSupplierDebt(debt.amount)
        }

    }

    suspend fun getClientFromDb(clientId:String) = clientsDao.getClient(clientId)

    suspend fun getSupplierFromDb(supplierId:String) = suppliersDao.getSupplier(supplierId)

    suspend fun getLastDebts(traderId: String) = debtsDao.getDebtsWithId(traderId)

    fun getAllClients(): Flow<PagingData<SimpleDebt>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ){
            clientsDao.getAllPaged()
        }.flow.map { pagingData -> pagingData.map { SimpleDebt(it.name, it.debt, it.clientId) } }
            .cachedIn(viewModelScope)
    }

    fun getAllSuppliers(): Flow<PagingData<SimpleDebt>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ){
            suppliersDao.getAllPaged()
        }.flow.map { pagingData -> pagingData.map { SimpleDebt(it.companyName, it.debt, it.supplierId) } }
            .cachedIn(viewModelScope)
    }

    fun getAllDebts(): Flow<PagingData<Debt>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ){
            debtsDao.getPagedDebts()
        }.flow.cachedIn(viewModelScope)
    }


    val businessDebts = debtsDao.getDebtsForBusiness()

}