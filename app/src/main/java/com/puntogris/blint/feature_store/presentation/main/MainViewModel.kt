package com.puntogris.blint.feature_store.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.puntogris.blint.feature_store.data.data_source.local.SharedPreferences
import com.puntogris.blint.feature_store.domain.repository.StoreRepository
import com.puntogris.blint.feature_store.domain.repository.TrafficRepository
import com.puntogris.blint.feature_store.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    trafficRepository: TrafficRepository,
    userRepository: UserRepository,
    storeRepository: StoreRepository
) : ViewModel() {

    val user = userRepository.getUserFlow().asLiveData()

    val currentBusiness = storeRepository.getCurrentStoreFlow().asLiveData()

    val lastTraffic = trafficRepository.getLastTrafficFlow(2).asLiveData()

    fun showLoginScreen() = sharedPreferences.showLoginScreen()

    fun showNewUserScreen() = sharedPreferences.showNewUserScreen()
}
