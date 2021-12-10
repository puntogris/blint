package com.puntogris.blint.feature_store.presentation.settings

import androidx.lifecycle.ViewModel
import com.puntogris.blint.feature_store.domain.repository.AuthRepository
import com.puntogris.blint.feature_store.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeleteAccountViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    suspend fun deleteAccount(email: String) = userRepository.deleteUserAccount(email)

    suspend fun signOut() = authRepository.signOutUser()
}
