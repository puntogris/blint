package com.puntogris.blint.ui.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.data.local.businesses.BusinessDao
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.utils.BusinessData
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val businessDao: BusinessDao): ViewModel() {

    fun logInUserToFirestore(credentialToken: String) =
        userRepository.logInUserWithCredentialToken(credentialToken)

    fun singOut(){
        userRepository.singOutCurrentUser()
    }

    suspend fun lookUpUserBusinessData(firebaseUser: FirebaseUser):BusinessData {
        val user = FirestoreUser(
            firebaseUser.uid,
            firebaseUser.displayName.toString(),
            firebaseUser.photoUrl.toString(),
            firebaseUser.email.toString()
        )
        return userRepository.checkUserDataInFirestore(user)
    }

}