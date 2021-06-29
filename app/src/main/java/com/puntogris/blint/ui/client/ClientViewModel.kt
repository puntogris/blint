package com.puntogris.blint.ui.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.puntogris.blint.data.local.dao.ClientsDao
import com.puntogris.blint.data.local.dao.OrdersDao
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.ClientRepository
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Record
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientsDao: ClientsDao,
    private val usersDao: UsersDao,
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _currentClient = MutableStateFlow(Client())
    val currentClient : LiveData<Client> = _currentClient.asLiveData()

    suspend fun getClientPaging() = clientRepository.getClientPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getClientsRecords(clientId: String) =
        clientRepository.getClientRecordsPagingDataFlow(clientId).cachedIn(viewModelScope)

    suspend fun saveClientDatabase() = clientRepository.saveClientDatabase(_currentClient.value)

    suspend fun deleteClientDatabase(clientId:String) = clientRepository.deleteClientDatabase(clientId)

    suspend fun getClientsWithName(name: String) =
        clientRepository.getClientWithNamePagingDataFlow(name).cachedIn(viewModelScope)

    suspend fun getClient(clientId:String) = clientsDao.getClient(clientId)

    fun setClientData(client: Client){
        _currentClient.value = client
    }

    fun updateClientData(client: Client){
        client.clientId = _currentClient.value.clientId
        _currentClient.value = client
    }

    suspend fun getCurrentBusiness() = usersDao.getUser()

}