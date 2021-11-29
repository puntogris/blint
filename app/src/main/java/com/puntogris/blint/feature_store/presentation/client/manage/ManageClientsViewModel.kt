package com.puntogris.blint.feature_store.presentation.client.manage

import android.text.Editable
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.feature_store.domain.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class ManageClientsViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val clientsFlow = query.flatMapLatest {
        clientRepository.getClientsPaged(it)
    }.cachedIn(viewModelScope)

    fun setQuery(editable: Editable) {
        query.value = editable.toString()
    }
}