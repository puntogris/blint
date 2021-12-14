package com.puntogris.blint.feature_store.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.puntogris.blint.feature_store.data.data_source.local.SharedPreferences
import com.puntogris.blint.feature_store.domain.repository.EventRepository
import com.puntogris.blint.feature_store.domain.repository.StoreRepository
import com.puntogris.blint.feature_store.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val userRepository: UserRepository,
    storeRepository: StoreRepository,
    eventRepository: EventRepository
) : ViewModel() {

    val user = userRepository.getUserFlow().asLiveData()

    fun showLoginScreen() = sharedPreferences.showLoginScreen()

    fun showNewUserScreen() = sharedPreferences.showNewUserScreen()

    val currentBusiness = storeRepository.getCurrentStoreFlow().asLiveData()

    val lastEventsFlow = eventRepository.getStoreLastEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

}