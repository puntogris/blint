package com.puntogris.blint.feature_store.presentation.debt.debt_status

import androidx.lifecycle.*
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.domain.model.Trader
import com.puntogris.blint.feature_store.domain.model.order.Debt
import com.puntogris.blint.feature_store.domain.repository.DebtsRepository
import com.puntogris.blint.feature_store.domain.repository.StatisticRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DebtStatusViewModel @Inject constructor(
    private val debtsRepository: DebtsRepository,
    statisticRepository: StatisticRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val trader = DebtStatusFragmentArgs.fromSavedStateHandle(savedStateHandle).trader

    val traderDebts = debtsRepository.getLastTraderDebts(trader.traderId)

    val totalDebtLiveData = statisticRepository.getCurrentBusinessStatistics().map {
        if (trader.traderType == Constants.CLIENT) it.clientsDebt else it.suppliersDebt
    }

    suspend fun saveDebt(amountSign: String, debtAmount: Float): SimpleResult {
        val debt = Debt().apply {
            amount = if (amountSign == "-") -debtAmount else debtAmount
            traderId = trader.traderId
            traderName = trader.traderName
            traderType = trader.traderType
        }
        return debtsRepository.saveDebt(debt)
    }
}