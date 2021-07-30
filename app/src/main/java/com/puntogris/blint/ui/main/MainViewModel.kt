package com.puntogris.blint.ui.main

import androidx.lifecycle.*
import com.puntogris.blint.data.repo.main.MainRepository
import com.puntogris.blint.model.BusinessCounters
import com.puntogris.blint.model.Employee
import com.puntogris.blint.utils.AccountStatus
import com.puntogris.blint.utils.RepoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
):ViewModel() {

    val businessCounter:LiveData<BusinessCounters> = liveData {
        emitSource(mainRepository.getBusinessCounterFlow().asLiveData())
    }

    suspend fun getSyncStatus(employee: List<Employee>) = mainRepository.checkIfAccountIsSynced(employee)

    private val _currentUser = MutableStateFlow(Employee())
    val currentUser:StateFlow<Employee> = _currentUser

    private val _accountStatus: MutableSharedFlow<AccountStatus> = MutableSharedFlow()
    val accountStatus: SharedFlow<AccountStatus> = _accountStatus

    init {
        viewModelScope.launch {
            _currentUser.emitAll(mainRepository.getCurrentUserFlow())
        }

        viewModelScope.launch {
            getBusinessStatus().collect {
                when(it){
                    is RepoResult.Error -> {}
                    RepoResult.InProgress -> {}
                    is RepoResult.Success -> {
                        _accountStatus.emit(getSyncStatus(it.data))
                    }
                }
            }
        }
    }

    fun isUserLoggedIn() = mainRepository.checkIfUserIsLogged()

    fun getUnreadNotificationsCount() = mainRepository.getAllUnreadNotifications()

    suspend fun getBusinessLastEvents() = mainRepository.getBusinessLastEventsDatabase()

    suspend fun getBusinessList() = mainRepository.getBusinessListRoom()

    suspend fun updateCurrentBusiness(id:String) = mainRepository.updateCurrentBusiness(id)

    fun getBusinessStatus() = mainRepository.getBusinessesStatus()
}