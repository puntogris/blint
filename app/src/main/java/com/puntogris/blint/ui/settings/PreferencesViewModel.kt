package com.puntogris.blint.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.data.local.dao.EmployeeDao
import com.puntogris.blint.data.remote.BackupRepository
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.ui.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val employeeDao: EmployeeDao,
    private val sharedPref: SharedPref,
    private val backupRepository: BackupRepository) :ViewModel() {

    private val _userData = MutableLiveData<FirebaseUser>(userRepository.getCurrentUser())
    val userData: LiveData<FirebaseUser> = _userData


    suspend fun logOut(){
        sharedPref.setWelcomeUiPref(false)
        sharedPref.setUserHasBusinessPref(false)
        employeeDao.deleteAll()
        userRepository.singOutCurrentUser()
    }

    suspend fun sendReport(message: String) =
        userRepository.sendReportToFirestore(message)

    suspend fun backupBusiness(path: String) =
        backupRepository.createBackupForBusiness(path)

    suspend fun restoreBackup(path: String) =
        backupRepository.restoreBackupForBusiness(path)

    fun getBackUpRequirements() = backupRepository.checkBackUpRequirements()

    fun getLastBackUpDate() = backupRepository.checkLastBackUpDate()
}