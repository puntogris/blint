package com.puntogris.blint.ui.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.data.local.businesses.BusinessDao
import com.puntogris.blint.data.remote.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val businessDao: BusinessDao): ViewModel() {

    fun isUserLoggedIn() = userRepository.checkIfUserIsLogged()

    fun logInUserToFirestore(credentialToken: String) =
        userRepository.logInUserWithCredentialToken(credentialToken)

    fun singOut(){
        userRepository.singOutCurrentUser()
    }

    suspend fun userHasBusinessRegistered() = businessDao.getCount() > 1



//    fun saveUserToDatabase(firebaseUser: FirebaseUser){
//        viewModelScope.launch {
//            RoomUser(
//                firebaseUser.uid,
//                firebaseUser.displayName.toString(),
//                firebaseUser.photoUrl.toString(),
//                firebaseUser.email.toString()).apply { userRepository.saveUserToDatabases(this) }
//        }
//    }

 //   suspend fun getCurrentUser() = userRepository.getCurrentUserFromDatabase()
}