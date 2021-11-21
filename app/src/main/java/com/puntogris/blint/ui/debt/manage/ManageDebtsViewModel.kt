package com.puntogris.blint.ui.debt.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.data.repository.debts.DebtsRepository
import com.puntogris.blint.data.repository.statistics.StatisticRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageDebtsViewModel @Inject constructor(
    debtsRepository: DebtsRepository,
    statisticRepository: StatisticRepository
) : ViewModel() {

    val businessStatistics = statisticRepository.getCurrentBusinessStatistics()

    val debtsFlow = debtsRepository.getDebtsPaged().cachedIn(viewModelScope)
}