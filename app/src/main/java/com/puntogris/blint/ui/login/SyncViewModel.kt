package com.puntogris.blint.ui.login

import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.repo.UserRepository
import com.puntogris.blint.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val userRepository: UserRepository
):ViewModel() {

    suspend fun syncAccount(userData: UserData? = null) = userRepository.syncAccountFromDatabase(userData)

}