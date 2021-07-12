package com.puntogris.blint.ui.debt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.puntogris.blint.data.local.dao.ClientsDao
import com.puntogris.blint.data.local.dao.DebtsDao
import com.puntogris.blint.data.local.dao.SuppliersDao
import com.puntogris.blint.data.repo.DebtsRepository
import com.puntogris.blint.model.Debt
import com.puntogris.blint.model.SimpleDebt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DebtViewModel @Inject constructor(
    private val suppliersDao: SuppliersDao,
    private val clientsDao: ClientsDao,
    private val debtsRepository: DebtsRepository
    ): ViewModel() {

    val businessDebts = liveData { emit(debtsRepository.getBusinessDebtData()) }

    suspend fun registerNewDebt(debt:Debt) = debtsRepository.registerNewDebtDatabase(debt)

    suspend fun getLastDebts(traderId: String) = debtsRepository.getLastTraderDebts(traderId)

    suspend fun getAllDebts() = debtsRepository.getBusinessDebtsPagingDataFlow().cachedIn(viewModelScope)

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
}