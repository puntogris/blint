package com.puntogris.blint.feature_store.presentation.client.detail

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.feature_store.domain.model.Client
import com.puntogris.blint.feature_store.domain.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val repository: ClientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val clientArg = savedStateHandle.getLiveData<Client>("client").asFlow()
    private val clientIdArg = savedStateHandle.getLiveData<String>("clientId").asFlow()

    val currentClient = combine(clientArg, clientIdArg){ client, id ->
        client ?: repository.getClient(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Client())

    @ExperimentalCoroutinesApi
    val clientsRecords = currentClient.flatMapLatest {
        repository.getClientRecordsPaged(it.clientId)
    }.cachedIn(viewModelScope)

    suspend fun deleteClient() = repository.deleteClient(currentClient.value.clientId)
}