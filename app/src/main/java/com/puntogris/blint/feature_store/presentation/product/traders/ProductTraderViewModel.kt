package com.puntogris.blint.feature_store.presentation.product.traders

import android.text.Editable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.puntogris.blint.common.utils.types.TraderQuery
import com.puntogris.blint.feature_store.domain.model.CheckableTrader
import com.puntogris.blint.feature_store.domain.model.Trader
import com.puntogris.blint.feature_store.domain.repository.TraderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ProductTraderViewModel @Inject constructor(
    private val repository: TraderRepository,
    handle: SavedStateHandle
) : ViewModel() {

    private val query = MutableStateFlow("")

    private val initialSuppliers =
        ProductTraderFragmentArgs.fromSavedStateHandle(handle).traders ?: emptyArray()

    private val initialSuppliersIds = initialSuppliers.map { it.traderId }

    private val finalTraders = initialSuppliers.toMutableList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val tradersFlows = query.flatMapLatest {
        repository.getTradersPaged(TraderQuery(query = it))
    }.map {
        it.map { trader ->
            CheckableTrader(
                trader = trader,
                isChecked = trader.traderId in initialSuppliersIds
            )
        }
    }.cachedIn(viewModelScope)

    fun setQuery(editable: Editable) {
        query.value = editable.toString()
    }

    fun toggleTrader(trader: Trader) {
        if (trader in finalTraders) finalTraders.remove(trader)
        else finalTraders.add(trader)
    }

    fun getFinalTraders() = finalTraders
}
