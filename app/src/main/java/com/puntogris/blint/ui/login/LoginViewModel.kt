package com.puntogris.blint.ui.login

import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.puntogris.blint.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: AuthRepository,
    private val googleSignInClient: GoogleSignInClient
) : ViewModel() {

    fun authGoogleUser(result: ActivityResult) = loginRepository.serverAuthWithGoogle(result)

    fun getGoogleSignInIntent() = googleSignInClient.signInIntent

}