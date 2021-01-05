package com.puntogris.blint.ui.client

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.local.clients.ClientsDao
import com.puntogris.blint.model.Client
import kotlinx.coroutines.flow.Flow

class ClientViewModel @ViewModelInject constructor(
    private val clientsDao: ClientsDao
) : ViewModel() {

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


}