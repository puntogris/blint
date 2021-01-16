package com.puntogris.blint.ui.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.data.local.businesses.BusinessDao
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.utils.BusinessData
import com.puntogris.blint.utils.RepoResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val businessDao: BusinessDao): ViewModel() {

    private val _userBusiness = MutableStateFlow(emptyList<Business>())
    val userBusiness: StateFlow<List<Business>> = _userBusiness

    fun logInUserToFirestore(credentialToken: String) =
        userRepository.logInUserWithCredentialToken(credentialToken)

    fun singOut(){
        userRepository.singOutCurrentUser()
    }

    suspend fun lookUpUserBusinessData(firebaseUser: FirebaseUser):RepoResult {
        val user = FirestoreUser(
            firebaseUser.uid,
            firebaseUser.displayName.toString(),
            firebaseUser.photoUrl.toString(),
            firebaseUser.email.toString()
        )
        return userRepository.checkUserDataInFirestore(user)
    }

    fun getUserBusiness() {
        viewModelScope.launch {
            _userBusiness.emitAll(userRepository.getBusinessForUser())
        }
    }

    suspend fun saveBusinessToLocalDatabase(){
        userBusiness.value.forEach {
            businessDao.insert(it)
        }
    }
}