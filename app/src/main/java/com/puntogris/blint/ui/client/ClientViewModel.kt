package com.puntogris.blint.ui.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.data.repository.clients.ClientRepository
import com.puntogris.blint.model.Client
import com.puntogris.blint.utils.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _currentClient = MutableStateFlow(Client())
    val currentClient : LiveData<Client> = _currentClient.asLiveData()

    suspend fun getClientsRecords(clientId: String) =
        clientRepository.getClientRecordsPagingDataFlow(clientId).cachedIn(viewModelScope)

    suspend fun saveClientDatabase():SimpleResult {
        _currentClient.value.name = _currentClient.value.name.lowercase()
        return clientRepository.saveClientDatabase(_currentClient.value)
    }

    suspend fun deleteClientDatabase(clientId:String) = clientRepository.deleteClientDatabase(clientId)

    fun setClientData(client: Client){
        _currentClient.value = client
    }

    fun updateClientData(client: Client){
        client.clientId = _currentClient.value.clientId
        _currentClient.value = client
    }
}