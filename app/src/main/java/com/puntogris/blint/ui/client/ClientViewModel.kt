package com.puntogris.blint.ui.client

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.local.dao.ClientsDao
import com.puntogris.blint.data.local.dao.RecordsDao
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Record
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ClientViewModel @ViewModelInject constructor(
    private val clientsDao: ClientsDao,
    private val recordsDao: RecordsDao,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao
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

    fun getClientsWithName(name: String): Flow<PagingData<Client>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            clientsDao.getPagedSearch("%${name}%")
        }.flow
    }

    suspend fun getClient(id:Int) = clientsDao.getClient(id)

    fun setClientData(client: Client){
        _currentClient.value = client
    }

    fun saveClientDatabase(){
        viewModelScope.launch(Dispatchers.IO) {
            _currentClient.value.businessId = getCurrentBusiness().currentBusinessId
            clientsDao.insert(_currentClient.value)
            if (_currentClient.value.clientId == 0 ) statisticsDao.incrementTotalClients()
        }
    }

    fun deleteClientDatabase(id:Int){
        viewModelScope.launch {
            clientsDao.delete(id)
            statisticsDao.decrementTotalClients()
        }
    }

    fun updateClientData(client: Client){
        client.clientId = _currentClient.value.clientId
        _currentClient.value = client
    }


    fun getClientsRecords(clientID:Int):Flow<PagingData<Record>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ){
            recordsDao.getClientsRecords(clientID)
        }.flow
    }

    suspend fun getCurrentBusiness() = usersDao.getUser()


}