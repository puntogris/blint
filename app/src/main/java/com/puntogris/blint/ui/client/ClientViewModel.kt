package com.puntogris.blint.ui.client

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.local.clients.ClientsDao
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ClientViewModel @ViewModelInject constructor(
    private val clientsDao: ClientsDao
) : ViewModel() {

    private val _currentClient = MutableStateFlow(Client())
    val currentClient : LiveData<Client> = _currentClient.asLiveData()

    fun getAllClients(): Flow<PagingData<Client>> {
        return Pager(
            PagingConfig(
            pageSize = 30,
            enablePlaceholders = true,
            maxSize = 200
        )
        ){
            clientsDao.getAllPaged()
        }.flow
    }

    suspend fun getClient(id:Int) = clientsDao.getClient(id)

    fun setClientData(client: Client){
        _currentClient.value = client
    }

    fun saveClientDatabase(){
        viewModelScope.launch {
            clientsDao.insert(_currentClient.value)
        }
    }

    fun deleteClientDatabase(){
        viewModelScope.launch {
            clientsDao.delete(_currentClient.value)
        }
    }

    fun updateClientData(client: Client){
        client.id = _currentClient.value.id
        _currentClient.value = client
    }
}