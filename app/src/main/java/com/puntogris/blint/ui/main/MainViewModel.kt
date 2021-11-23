package com.puntogris.blint.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.puntogris.blint.data.data_source.local.SharedPreferences
import com.puntogris.blint.data.repository.main.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    mainRepository: MainRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val businessCounter = mainRepository.getBusinessCounterFlow().asLiveData()

    fun showLogin() = sharedPreferences.showLoginScreen()

    val lastEventsFlow = mainRepository.getBusinessLastEventsDatabase()
}