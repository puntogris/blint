package com.puntogris.blint.feature_store.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.puntogris.blint.feature_store.data.data_source.local.SharedPreferences
import com.puntogris.blint.feature_store.domain.repository.BusinessRepository
import com.puntogris.blint.feature_store.domain.repository.EventRepository
import com.puntogris.blint.feature_store.domain.repository.ReportsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    businessRepository: BusinessRepository,
    eventRepository: EventRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    fun showLogin() = sharedPreferences.showLoginScreen()

    val currentBusiness = businessRepository.getCurrentBusinessFlow().asLiveData()

    val lastEventsFlow = eventRepository.getBusinessLastEventsDatabase()
}