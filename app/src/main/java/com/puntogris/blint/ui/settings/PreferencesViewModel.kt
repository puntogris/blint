package com.puntogris.blint.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.data.local.dao.EmployeeDao
import com.puntogris.blint.data.repo.backup.BackupRepository
import com.puntogris.blint.data.repo.UserRepository
import com.puntogris.blint.data.repo.login.LoginRepository
import com.puntogris.blint.ui.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val backupRepository: BackupRepository,
    private val loginRepository: LoginRepository
) :ViewModel() {

    private val _userData = MutableLiveData<FirebaseUser>(userRepository.getCurrentUser())
    val userData: LiveData<FirebaseUser> = _userData

    suspend fun logOut() = loginRepository.signOutUser()

    suspend fun sendReport(message: String) =
        userRepository.sendReportToFirestore(message)

    fun backupBusiness(path: String) = backupRepository.createBackupForBusiness(path)

    fun restoreBackup(path: String) = backupRepository.restoreBackupForBusiness(path)

    fun getBackUpRequirements() = backupRepository.checkBackUpRequirements()

    fun getLastBackUpDate() = backupRepository.checkLastBackUpDate()

    suspend fun getUserBusinesses() = userRepository.getUserBusiness()
}