package com.puntogris.blint.feature_store.presentation.client.create_edit

import android.net.Uri
import android.text.Editable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.puntogris.blint.common.framework.ContactsHelper
import com.puntogris.blint.feature_store.domain.model.Client
import com.puntogris.blint.feature_store.domain.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EditClientViewModel @Inject constructor(
    private val repository: ClientRepository,
    private val savedStateHandle: SavedStateHandle,
    private val contactsHelper: ContactsHelper
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

    fun setContact(uri: Uri) {
        contactsHelper.getContact(uri)?.let {
            savedStateHandle.set(
                "client",
                _currentClient.value.copy(
                    name = it.name,
                    email = it.email,
                    address = it.address,
                    phone = it.phone
                )
            )
        }
    }
}