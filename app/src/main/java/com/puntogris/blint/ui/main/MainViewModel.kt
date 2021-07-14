package com.puntogris.blint.ui.main

import androidx.lifecycle.*
import com.puntogris.blint.data.repo.MainRepository
import com.puntogris.blint.model.BusinessCounters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
):ViewModel() {

    val businessCounter:LiveData<BusinessCounters> = liveData {
        emitSource(mainRepository.getBusinessCounterFlow().asLiveData())
    }

    fun isUserLoggedIn() = mainRepository.checkIfUserIsLogged()

    fun getUnreadNotificationsCount() = mainRepository.getAllUnreadNotifications()

    suspend fun getBusinessLastEvents() = mainRepository.getBusinessLastEventsDatabase()

    fun getCurrentBusiness() = mainRepository.getUserLiveDataRoom()

    suspend fun getBusinessList() = mainRepository.getBusinessListRoom()

    suspend fun updateCurrentBusiness(id:String, name:String, type:String, owner: String) = mainRepository.updateCurrentBusiness(id,name,type,owner)
}