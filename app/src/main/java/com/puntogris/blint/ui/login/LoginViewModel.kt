package com.puntogris.blint.ui.login

import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.data.repository.login.LoginRepository
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.utils.RegistrationData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val googleSignInClient: GoogleSignInClient
): ViewModel() {

    fun authGoogleUser(result: ActivityResult) = loginRepository.serverAuthWithGoogle(result)

    fun getGoogleSignInIntent() = googleSignInClient.signInIntent

    suspend fun registerAnonymousUser() = loginRepository.singInAnonymously()


}