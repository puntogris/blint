package com.puntogris.blint.ui.debt.debt_status

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.puntogris.blint.data.repository.debts.DebtsRepository
import com.puntogris.blint.data.repository.statistics.StatisticRepository
import com.puntogris.blint.model.Trader
import com.puntogris.blint.model.order.Debt
import com.puntogris.blint.utils.Constants
import com.puntogris.blint.utils.types.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DebtStatusViewModel @Inject constructor(
    private val debtsRepository: DebtsRepository,
    statisticRepository: StatisticRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val trader = savedStateHandle.get<Trader>("trader")!!

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