package com.puntogris.blint.feature_store.presentation.trader.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.common.framework.ContactsHelper
import com.puntogris.blint.feature_store.domain.model.Trader
import com.puntogris.blint.feature_store.domain.repository.DebtRepository
import com.puntogris.blint.feature_store.domain.repository.TraderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TraderViewModel @Inject constructor(
    private val traderRepository: TraderRepository,
    private val debtRepository: DebtRepository,
    private val contactsHelper: ContactsHelper,
    handle: SavedStateHandle
) : ViewModel() {

    val currentTrader = handle.getLiveData<String>("traderId").asFlow().flatMapLatest {
        traderRepository.getTraderFlow(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Trader())

    val traderRecords = currentTrader.flatMapLatest {
        traderRepository.getTradersRecordsPaged(it.traderId)
    }.cachedIn(viewModelScope)

    val traderDebts = currentTrader.flatMapLatest {
        debtRepository.getLastTraderDebts(it.traderId)
    }.cachedIn(viewModelScope)

    suspend fun deleteTrader() = traderRepository.deleteTrader(currentTrader.value.traderId)

    fun getContactIntent() = contactsHelper.getSaveContactIntent(currentTrader.value)
}
