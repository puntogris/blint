package com.puntogris.blint.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.puntogris.blint.data.repository.backup.BackupRepository
import com.puntogris.blint.data.repository.login.LoginRepository
import com.puntogris.blint.data.repository.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val backupRepository: BackupRepository,
    private val loginRepository: LoginRepository
) : ViewModel() {

    val userData = liveData {
        emit(userRepository.getCurrentUser())
    }

    suspend fun logOut() = loginRepository.signOutUser()

    fun backupBusiness(path: String) = backupRepository.createBackupForBusiness(path)

    fun restoreBackup(path: String) = backupRepository.restoreBackupForBusiness(path)

    fun getBackUpRequirements() = backupRepository.checkBackUpRequirements()

    fun getLastBackUpDate() = backupRepository.checkLastBackUpDate()
}