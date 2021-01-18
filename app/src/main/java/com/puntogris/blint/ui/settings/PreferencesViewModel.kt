package com.puntogris.blint.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.data.remote.BackupRepository
import com.puntogris.blint.data.remote.UserRepository
import kotlinx.coroutines.launch

class PreferencesViewModel @ViewModelInject constructor(
    private val userRepository: UserRepository,
    private val backupRepository: BackupRepository) :ViewModel() {

    private val _userData = MutableLiveData<FirebaseUser>(userRepository.getCurrentUser())
    val userData:LiveData<FirebaseUser> = _userData

    fun logOut(){
        userRepository.singOutCurrentUser()
    }

    suspend fun sendReport(message: String) =
        userRepository.sendReportToFirestore(message)

    suspend fun backupBusiness(businessID:String, paths: List<String>) =
        backupRepository.createBackupForBusiness(businessID, paths)

    suspend fun restoreBackup(businessID: String ,paths: List<String>) =
        backupRepository.restoreBackupForBusiness(businessID, paths)

    fun getOwnerBusiness() = userRepository.getOwnerBusiness()


}