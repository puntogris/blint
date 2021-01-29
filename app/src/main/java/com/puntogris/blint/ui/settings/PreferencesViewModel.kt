package com.puntogris.blint.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.data.local.dao.BusinessDao
import com.puntogris.blint.data.remote.BackupRepository
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.ui.SharedPref
import kotlinx.coroutines.launch

class PreferencesViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val businessDao: BusinessDao,
    private val sharedPref: SharedPref,
    private val backupRepository: BackupRepository) :ViewModel() {

    private val _userData = MutableLiveData<FirebaseUser>(userRepository.getCurrentUser())
    val userData: LiveData<FirebaseUser> = _userData

    suspend fun logOut(){
        sharedPref.setUserHasBusinessRegisteredPref(false)
        businessDao.deleteAll()
        userRepository.singOutCurrentUser()
    }

    suspend fun sendReport(message: String) =
        userRepository.sendReportToFirestore(message)

    suspend fun backupBusiness(businessID: String, path: String) =
        backupRepository.createBackupForBusiness(businessID, path)

    suspend fun restoreBackup(businessID: String ,path: String) =
        backupRepository.restoreBackupForBusiness(businessID, path)

    fun getOwnerBusiness() = userRepository.getOwnerBusiness()
}