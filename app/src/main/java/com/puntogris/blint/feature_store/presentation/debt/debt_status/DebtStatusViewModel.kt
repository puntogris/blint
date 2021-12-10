package com.puntogris.blint.feature_store.presentation.debt.debt_status

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.feature_store.domain.model.order.Debt
import com.puntogris.blint.feature_store.domain.repository.BusinessRepository
import com.puntogris.blint.feature_store.domain.repository.DebtsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DebtStatusViewModel @Inject constructor(
    private val debtsRepository: DebtsRepository,
    businessRepository: BusinessRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val trader = DebtStatusFragmentArgs.fromSavedStateHandle(savedStateHandle).trader

    val traderDebts = debtsRepository.getLastTraderDebts(trader.traderId)

    val totalDebtLiveData = businessRepository.getCurrentBusinessFlow().map {
        if (trader.traderType == Constants.CLIENT) it.clientsDebt else it.suppliersDebt
    }.asLiveData()

    suspend fun saveDebt(amountSign: String, debtAmount: Float): SimpleResource {
        val debt = Debt().apply {
            amount = if (amountSign == "-") -debtAmount else debtAmount
            traderId = trader.traderId
            traderName = trader.traderName
            traderType = trader.traderType
        }
        return debtsRepository.saveDebt(debt)
    }
}