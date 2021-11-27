package com.puntogris.blint.feature_store.presentation.client.manage

import android.text.Editable
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.feature_store.domain.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageClientsViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val query = MutableLiveData("")

    val clientsLiveData = query.switchMap {
        clientRepository.getClientsPaged(it).asLiveData()
    }.cachedIn(viewModelScope)

    fun setQuery(editable: Editable) {
        query.value = editable.toString()
    }
}