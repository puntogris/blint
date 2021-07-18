package com.puntogris.blint.ui.main

import androidx.lifecycle.*
import com.puntogris.blint.data.repo.MainRepository
import com.puntogris.blint.model.BusinessCounters
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.RoomUser
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
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

    private val _currentUser = MutableStateFlow(RoomUser())
    val currentUser:StateFlow<RoomUser> = _currentUser

    init {
        viewModelScope.launch {
            _currentUser.emitAll(mainRepository.getCurrentUserFlow())
        }
    }

    fun isUserLoggedIn() = mainRepository.checkIfUserIsLogged()

    fun getUnreadNotificationsCount() = mainRepository.getAllUnreadNotifications()

    suspend fun getBusinessLastEvents() = mainRepository.getBusinessLastEventsDatabase()

    suspend fun getBusinessList() = mainRepository.getBusinessListRoom()

    suspend fun updateCurrentBusiness(id:String, name:String, type:String, owner: String, status:String) = mainRepository.updateCurrentBusiness(id,name,type,owner,status)

    fun getBusinessStatus() = mainRepository.getBusinessesStatus()

}