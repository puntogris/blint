package com.puntogris.blint.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.data.remote.BackupRepository
import com.puntogris.blint.data.remote.UserRepository

class PreferencesViewModel @ViewModelInject constructor(private val userRepository: UserRepository,
private val backupRepository: BackupRepository) :ViewModel() {

    private val _userData = MutableLiveData<FirebaseUser>(userRepository.getCurrentUser())
    val userData:LiveData<FirebaseUser> = _userData

    fun logOut(){
        userRepository.singOutCurrentUser()
    }

    suspend fun sendReport(message: String) =
        userRepository.sendReportToFirestore(message)

    fun backupBusiness(list: List<String>){
        backupRepository.createBackupForBusiness(list)
    }
}