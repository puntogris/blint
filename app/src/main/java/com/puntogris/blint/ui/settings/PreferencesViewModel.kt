package com.puntogris.blint.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.puntogris.blint.data.repository.login.LoginRepository
import com.puntogris.blint.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    userRepository: UserRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    val userData = userRepository.getUserFlow().asLiveData()

    suspend fun logOut() = loginRepository.signOutUser()
}