package com.puntogris.blint.feature_store.presentation.client.create_edit

import android.text.Editable
import androidx.lifecycle.*
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.domain.model.Client
import com.puntogris.blint.feature_store.domain.repository.ClientRepository
import com.puntogris.blint.feature_store.presentation.client.detail.ClientFragmentArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class EditClientViewModel @Inject constructor(
    private val repository: ClientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _currentClient = savedStateHandle.getLiveData<Client>("client")
        .asFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Client())

    val currentClient: StateFlow<Client> = _currentClient

    suspend fun saveClient() = repository.saveClient(_currentClient.value)

    fun updateClientName(editable: Editable) {
        _currentClient.value.name = editable.toString()
    }

    fun updateClientAddress(editable: Editable) {
        _currentClient.value.address = editable.toString()
    }

    fun updateClientEmail(editable: Editable) {
        _currentClient.value.email = editable.toString()
    }

    fun updateClientPhone(editable: Editable) {
        _currentClient.value.phone = editable.toString()
    }

    fun updateClientPaymentInfo(editable: Editable) {
        _currentClient.value.paymentInfo = editable.toString()
    }

    fun updateClientDiscount(editable: Editable) {
        _currentClient.value.discount = editable.toString().toFloatOrNull() ?: 0F
    }
}