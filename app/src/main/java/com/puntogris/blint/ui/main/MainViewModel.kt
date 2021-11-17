package com.puntogris.blint.ui.main

import androidx.lifecycle.*
import com.puntogris.blint.data.repository.main.MainRepository
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.BusinessCounters
import com.puntogris.blint.ui.SharedPreferences
import com.puntogris.blint.utils.types.AccountStatus
import com.puntogris.blint.utils.types.RepoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val businessCounter: LiveData<BusinessCounters> = liveData {
        emitSource(mainRepository.getBusinessCounterFlow().asLiveData())
    }

    private suspend fun getSyncStatus(business: List<Business>) =
        mainRepository.checkIfAccountIsSynced(business)

    private val _currentUser = MutableStateFlow(Business())
    val currentUser: StateFlow<Business> = _currentUser

    private val _accountStatus: MutableSharedFlow<AccountStatus> = MutableSharedFlow()
    val accountStatus: SharedFlow<AccountStatus> = _accountStatus

    init {
        viewModelScope.launch {
            _currentUser.emitAll(mainRepository.getCurrentUserFlow())
        }

        viewModelScope.launch {
            getBusinessStatus().collect {
                when (it) {
                    is RepoResult.Error -> {}
                    RepoResult.InProgress -> {}
                    is RepoResult.Success -> {
                        _accountStatus.emit(getSyncStatus(it.data))
                    }
                }
            }
        }
    }

    fun showLogin() = sharedPreferences.showLoginScreen()

    fun isUserLoggedIn() = mainRepository.checkIfUserIsLogged()

    val lastEventsFlow = mainRepository.getBusinessLastEventsDatabase()

    suspend fun getBusinessList() = mainRepository.getBusinessListRoom()

    suspend fun updateCurrentBusiness(id: String) = mainRepository.updateCurrentBusiness(id)

    private fun getBusinessStatus() = mainRepository.getBusinessesStatus()
}