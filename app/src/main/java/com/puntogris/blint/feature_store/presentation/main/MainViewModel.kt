package com.puntogris.blint.feature_store.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.puntogris.blint.feature_store.data.data_source.local.SharedPreferences
import com.puntogris.blint.feature_store.domain.repository.BusinessRepository
import com.puntogris.blint.feature_store.domain.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    businessRepository: BusinessRepository,
    eventRepository: EventRepository
) : ViewModel() {

    fun showLoginScreen() = sharedPreferences.showLoginScreen()

    fun showNewUserScreen() = sharedPreferences.showNewUserScreen()

    val currentBusiness = businessRepository.getCurrentBusinessFlow().asLiveData()

    val lastEventsFlow = eventRepository.getBusinessLastEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

}