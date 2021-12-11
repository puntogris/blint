package com.puntogris.blint.feature_store.presentation.debt.trader_debts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.puntogris.blint.feature_store.domain.model.SimpleDebt
import com.puntogris.blint.feature_store.domain.repository.TraderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ShowDebtsViewModel @Inject constructor(
    traderRepository: TraderRepository,
) : ViewModel() {

    val debtsFlow = traderRepository.getTradersPaged().map { pagingData ->
        pagingData.map {
            SimpleDebt(it.name, it.debt, it.traderId)
        }
    }.cachedIn(viewModelScope)

}