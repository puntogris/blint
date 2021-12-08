package com.puntogris.blint.feature_store.presentation.client.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.common.framework.ContactsHelper
import com.puntogris.blint.feature_store.domain.model.Client
import com.puntogris.blint.feature_store.domain.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val repository: ClientRepository,
    private val contactsHelper: ContactsHelper,
    handle: SavedStateHandle
) : ViewModel() {

    private val clientArg = handle.getLiveData<Client>("client").asFlow()
    private val clientIdArg = handle.getLiveData<String>("clientId").asFlow()

    val currentClient = combine(clientArg, clientIdArg) { client, id ->
        client ?: repository.getClient(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Client())

    @OptIn(ExperimentalCoroutinesApi::class)
    val clientsRecords = currentClient.flatMapLatest {
        repository.getClientRecordsPaged(it.clientId)
    }.cachedIn(viewModelScope)

    suspend fun deleteClient() = repository.deleteClient(currentClient.value.clientId)

    fun getContactIntent() = contactsHelper.getSaveContactIntent(currentClient.value)
}