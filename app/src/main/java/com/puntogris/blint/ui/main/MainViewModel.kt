package com.puntogris.blint.ui.main

import androidx.lifecycle.*
import com.puntogris.blint.data.local.dao.*
import com.puntogris.blint.data.repo.MainRepository
import com.puntogris.blint.data.repo.UserRepository
import com.puntogris.blint.model.BusinessCounters
import com.puntogris.blint.model.Statistic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val usersDao: UsersDao,
    private val employeeDao: EmployeeDao,
    private val mainRepository: MainRepository
):ViewModel() {

    val businessCounter:LiveData<BusinessCounters> = liveData {
        emitSource(mainRepository.getBusinessCounterFlow().asLiveData())
    }

    fun isUserLoggedIn() = mainRepository.checkIfUserIsLogged()

    fun getUnreadNotificationsCount() = mainRepository.getAllUnreadNotifications()

    suspend fun getBusinessLastEvents() = mainRepository.getBusinessLastEventsDatabase()

    fun getCurrentBusiness() = usersDao.getUserLiveData()

    suspend fun getBusinessList() = employeeDao.getEmployeesList()

    suspend fun updateCurrentBusiness(id:String, name:String, type:String, owner: String) = usersDao.updateCurrentBusiness(id, name, type, owner)
}