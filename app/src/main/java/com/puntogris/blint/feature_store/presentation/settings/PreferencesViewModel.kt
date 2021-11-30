package com.puntogris.blint.feature_store.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.puntogris.blint.feature_store.domain.model.User
import com.puntogris.blint.feature_store.domain.repository.AuthRepository
import com.puntogris.blint.feature_store.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser = userRepository.getUserFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    suspend fun logOut() = authRepository.signOutUser()
}

