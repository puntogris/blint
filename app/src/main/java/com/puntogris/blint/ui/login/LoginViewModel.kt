package com.puntogris.blint.ui.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.data.local.dao.BusinessDao
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.model.RoomUser
import com.puntogris.blint.model.Statistic
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val businessDao: BusinessDao,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao): ViewModel() {

    private val _userBusiness = MutableStateFlow(emptyList<Business>())
    val userBusiness: StateFlow<List<Business>> = _userBusiness

    fun logInUserToFirestore(credentialToken: String) =
        userRepository.logInUserWithCredentialToken(credentialToken)

    fun singOut(){
        viewModelScope.launch {
            businessDao.deleteAll()
            userRepository.singOutCurrentUser()
        }
    }

    suspend fun lookUpUserBusinessData(firebaseUser: FirebaseUser):SimpleResult {
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
            _userBusiness.emitAll(userRepository.getEmployeeBusiness())
        }
    }


    suspend fun saveBusinessToLocalDatabase(){
        businessDao.insert(userBusiness.value)
        usersDao.insert(RoomUser(
            currentBusinessId = userBusiness.value.first().businessId,
            currentBusinessType = userBusiness.value.first().type,
            currentBusinessName = userBusiness.value.first().name,
            currentUid = userRepository.getCurrentUID()
        ))
        statisticsDao.insert(userBusiness.value.map {
            Statistic(businessId = it.businessId)
        })

    }

    suspend fun registerNewBusiness(name: String){
        userRepository.getOwnerBusiness()
        userRepository.registerNewBusiness(name)
    }

}