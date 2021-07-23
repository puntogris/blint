package com.puntogris.blint.ui.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.data.repo.login.LoginRepository
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.utils.RegistrationData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
    ): ViewModel() {

    fun logInUserToFirestore(credential:String) = loginRepository.logInUserWithCredentialToken(credential)

    suspend fun singOut() = loginRepository.signOutUser()

    suspend fun lookUpUserBusinessData(firebaseUser: FirebaseUser): RegistrationData {
        return loginRepository.checkUserDataInFirestore(
                FirestoreUser(
                firebaseUser.uid,
                firebaseUser.displayName.toString(),
                firebaseUser.photoUrl.toString(),
                firebaseUser.email.toString()
                )
        )
    }
}