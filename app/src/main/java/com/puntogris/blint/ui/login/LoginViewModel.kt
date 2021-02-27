package com.puntogris.blint.ui.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.data.local.dao.EmployeeDao
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.FirestoreUser
import com.puntogris.blint.model.RoomUser
import com.puntogris.blint.model.Statistic
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.launch

class LoginViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val employeeDao: EmployeeDao,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao): ViewModel() {

    fun logInUserToFirestore(credentialToken: String) =
        userRepository.logInUserWithCredentialToken(credentialToken)

    fun singOut(){
        viewModelScope.launch {
            employeeDao.deleteAll()
            userRepository.singOutCurrentUser()
        }
    }

    suspend fun lookUpUserBusinessData(firebaseUser: FirebaseUser): SimpleResult {
        val user = FirestoreUser(
            firebaseUser.uid,
            firebaseUser.displayName.toString(),
            firebaseUser.photoUrl.toString(),
            firebaseUser.email.toString()
        )
        return userRepository.checkUserDataInFirestore(user)
    }

    fun getUserBusiness() = userRepository.getEmployeeBusiness()

    suspend fun saveBusinessToLocalDatabase(employees:List<Employee>, username: String, country: String){
        employeeDao.insert(employees)
        usersDao.insert(RoomUser(
            username = username,
            country = country,
            currentBusinessId = employees.first().businessId,
            currentBusinessType = employees.first().businessType,
            currentBusinessName = employees.first().name,
            currentUid = userRepository.getCurrentUID()
        ))
        statisticsDao.insert(employees.map {
            Statistic(businessId = it.businessId)
        })
    }

    suspend fun saveUserData(username: String, country: String){
        usersDao.insert(RoomUser(
            username = username,
            country = country,
            currentUid = userRepository.getCurrentUID()
        ))
    }

    suspend fun registerNewBusiness(name: String){
        userRepository.getOwnerBusiness()
        when(val result = userRepository.registerNewBusiness(name)){
            is RepoResult.Error -> {}
            is RepoResult.Success -> {
                statisticsDao.insert(Statistic(businessId = result.data))
            }
        }

    }

    suspend fun updateCurrentBusiness(business:Employee){
        usersDao.insert(RoomUser(
            currentBusinessId = business.businessId,
            currentBusinessType = business.businessType,
            currentBusinessName = business.name,
            currentUid = userRepository.getCurrentUID()
        ))
    }


    suspend fun updateUserData(username:String, country: String) =
        userRepository.updateUserNameCountry(username,country)

}