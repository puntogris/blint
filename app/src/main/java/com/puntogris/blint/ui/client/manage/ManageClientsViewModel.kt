package com.puntogris.blint.ui.client.manage

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.data.repository.clients.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageClientsViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _query = MutableLiveData<String>()
    val query: LiveData<String> get() = _query

    val clientsPaged = _query.switchMap {
        if (it.isNullOrBlank()) clientRepository.getAllClientsPaged().asLiveData()
        else clientRepository.getClientsWithNamePaged(it).asLiveData()
    }.cachedIn(viewModelScope)

    fun setQuery(query: String){
        _query.value = query
    }
}