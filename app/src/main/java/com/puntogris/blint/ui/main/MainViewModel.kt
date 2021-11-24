package com.puntogris.blint.ui.main

import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.data_source.local.SharedPreferences
import com.puntogris.blint.domain.repository.EventRepository
import com.puntogris.blint.domain.repository.StatisticRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    statisticRepository: StatisticRepository,
    eventRepository: EventRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val businessCounter = statisticRepository.getCurrentBusinessStatistics()

    fun showLogin() = sharedPreferences.showLoginScreen()

    val lastEventsFlow = eventRepository.getBusinessLastEventsDatabase()
}