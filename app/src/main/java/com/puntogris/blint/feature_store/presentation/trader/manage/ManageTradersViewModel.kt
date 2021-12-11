package com.puntogris.blint.feature_store.presentation.trader.manage

import android.text.Editable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.common.utils.types.TraderFilter
import com.puntogris.blint.common.utils.types.TraderQuery
import com.puntogris.blint.feature_store.domain.repository.TraderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class ManageTradersViewModel @Inject constructor(
    private val repository: TraderRepository
) : ViewModel() {

    private val query = MutableStateFlow(TraderQuery())

    @OptIn(ExperimentalCoroutinesApi::class)
    val tradersFlow = query.flatMapLatest {
        repository.getTradersPaged(it)
    }.cachedIn(viewModelScope)

    fun setQuery(editable: Editable) {
        query.value = query.value.copy(
            query = editable.toString()
        )
    }

    fun setFilter(filter: TraderFilter) {
        query.value = query.value.copy(
            filter = filter
        )
    }
}
