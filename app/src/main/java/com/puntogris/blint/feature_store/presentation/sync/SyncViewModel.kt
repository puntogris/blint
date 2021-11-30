package com.puntogris.blint.feature_store.presentation.sync

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.puntogris.blint.feature_store.data.data_source.local.SharedPreferences
import com.puntogris.blint.feature_store.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sharedPreferences: SharedPreferences,
    handle: SavedStateHandle
) : ViewModel() {

    private val authUser = SyncAccountFragmentArgs.fromSavedStateHandle(handle).authUser

    suspend fun syncAccount() = userRepository.syncUserAccount(authUser)

    fun showWelcome() = sharedPreferences.showNewUserScreen()
}