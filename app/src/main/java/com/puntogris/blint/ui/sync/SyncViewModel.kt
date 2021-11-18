package com.puntogris.blint.ui.sync

import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.data_source.local.SharedPreferences
import com.puntogris.blint.data.repository.user.UserRepository
import com.puntogris.blint.model.AuthUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    suspend fun syncAccount(authUser: AuthUser?) =
        userRepository.syncUserAccount(authUser)

    fun showWelcome() = sharedPreferences.showWelcomeScreen()

}