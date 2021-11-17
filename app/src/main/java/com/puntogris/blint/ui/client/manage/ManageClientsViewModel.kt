package com.puntogris.blint.ui.client.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.data.repository.clients.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageClientsViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    suspend fun getClientPaging() =
        clientRepository.getClientPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getClientsWithName(name: String) =
        clientRepository.getClientWithNamePagingDataFlow(name).cachedIn(viewModelScope)
}