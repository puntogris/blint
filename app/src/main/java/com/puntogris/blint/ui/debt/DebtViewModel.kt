package com.puntogris.blint.ui.debt

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.puntogris.blint.data.local.dao.ClientsDao
import com.puntogris.blint.data.local.dao.DebtsDao
import com.puntogris.blint.data.local.dao.SuppliersDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.model.Debt
import com.puntogris.blint.utils.Constants.CLIENT_DEBT
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DebtViewModel @Inject constructor(
    private val debtsDao: DebtsDao,
    private val suppliersDao: SuppliersDao,
    private val clientsDao: ClientsDao,
    private val userRepository: UserRepository
    ): ViewModel() {

    private val debt = Debt()

    fun updateDebtWithTraderInfo(id: Int, name: String, businessId:String){
        debt.traderId = id
        debt.traderName = name
        debt.businessId = businessId
    }

    fun getDebtData() = debt

    suspend fun registerNewDebt(debt:Debt, debtType: Int){
        debt.author = userRepository.getCurrentUser()?.email!!
        debt.timestamp = Timestamp.now()
        debtsDao.insert(debt)
        if (debtType == CLIENT_DEBT){
            clientsDao.updateClientDebt(debt.traderId, debt.amount)
        }else{
            suppliersDao.updateSupplierDebt(debt.traderId, debt.amount)
        }

    }

    suspend fun getClientFromDb(clientId:Int) = clientsDao.getClient(clientId)

    suspend fun getSupplierFromDb(supplierId:Int) = suppliersDao.getSupplier(supplierId)

    suspend fun getLastDebts(traderId: Int) = debtsDao.getDebtsWithId(traderId)
}