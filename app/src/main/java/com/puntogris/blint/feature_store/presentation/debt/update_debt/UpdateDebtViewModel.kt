package com.puntogris.blint.feature_store.presentation.debt.update_debt

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.puntogris.blint.common.utils.types.SimpleResource
import com.puntogris.blint.feature_store.domain.model.order.Debt
import com.puntogris.blint.feature_store.domain.repository.DebtRepository
import com.puntogris.blint.feature_store.domain.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UpdateDebtViewModel @Inject constructor(
    private val debtRepository: DebtRepository,
    storeRepository: StoreRepository
) : ViewModel() {



    val totalDebtLiveData = storeRepository.getCurrentStoreFlow().asLiveData()

//    suspend fun saveDebt(amountSign: String, debtAmount: Float): SimpleResource {
//        val debt = Debt().apply {
//            amount = if (amountSign == "-") -debtAmount else debtAmount
//            traderId = trader.traderId
//            traderName = trader.name
//            traderType = trader.type
//        }
//        return debtRepository.saveDebt(debt)
//    }
}