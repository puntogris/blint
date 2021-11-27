package com.puntogris.blint.feature_store.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.puntogris.blint.feature_store.domain.repository.AuthRepository
import com.puntogris.blint.feature_store.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val userData = userRepository.getUserFlow().asLiveData()

    suspend fun logOut() = authRepository.signOutUser()
}