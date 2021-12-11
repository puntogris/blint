package com.puntogris.blint.feature_store.presentation.trader.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.common.framework.ContactsHelper
import com.puntogris.blint.feature_store.domain.model.Trader
import com.puntogris.blint.feature_store.domain.repository.TraderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TraderViewModel @Inject constructor(
    private val repository: TraderRepository,
    private val contactsHelper: ContactsHelper,
    handle: SavedStateHandle
) : ViewModel() {

    private val traderArg = handle.getLiveData<Trader>("trader").asFlow()
    private val traderIdArg = handle.getLiveData<String>("traderId").asFlow()

    val currentTrader = combine(traderArg, traderIdArg) { trader, id ->
        trader ?: repository.getTrader(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Trader())

    @OptIn(ExperimentalCoroutinesApi::class)
    val clientsRecords = currentTrader.flatMapLatest {
        repository.getTradersRecordsPaged(it.traderId)
    }.cachedIn(viewModelScope)

    suspend fun deleteTrader() = repository.deleteTrader(currentTrader.value.traderId)

    fun getContactIntent() = contactsHelper.getSaveContactIntent(currentTrader.value)
}