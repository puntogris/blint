package com.puntogris.blint.feature_store.presentation.debt.update_debt

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.feature_store.domain.model.Trader
import com.puntogris.blint.feature_store.domain.model.order.Debt
import com.puntogris.blint.feature_store.domain.repository.DebtRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UpdateDebtViewModel @Inject constructor(
    private val debtRepository: DebtRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val trader = savedStateHandle.get<Trader>("trader") ?: Trader()

    suspend fun saveDebt(debtAmount: Float): SimpleResource {
        val debt = Debt().apply {
            amount = debtAmount
            traderId = trader.traderId
            traderName = trader.name
            traderType = trader.type
        }
        return debtRepository.saveDebt(debt)
    }
}