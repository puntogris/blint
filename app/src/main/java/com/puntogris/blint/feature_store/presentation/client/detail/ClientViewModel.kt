package com.puntogris.blint.feature_store.presentation.client.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.feature_store.domain.model.Client
import com.puntogris.blint.feature_store.domain.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val repository: ClientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val client = savedStateHandle.get<Client>("client") ?: Client()

    private val _currentClient = MutableStateFlow(client)
    val currentClient = _currentClient.asStateFlow()

    val clientsRecords = repository.getClientRecordsPaged(client.clientId).cachedIn(viewModelScope)

    suspend fun deleteClient(clientId: String) = repository.deleteClient(clientId)
}