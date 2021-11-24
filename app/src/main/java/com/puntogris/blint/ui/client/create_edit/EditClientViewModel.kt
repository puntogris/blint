package com.puntogris.blint.ui.client.create_edit

import android.text.Editable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.puntogris.blint.domain.repository.ClientRepository
import com.puntogris.blint.model.Client
import com.puntogris.blint.utils.types.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class EditClientViewModel @Inject constructor(
    private val repository: ClientRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val client = savedStateHandle.get<Client>("client") ?: Client()

    private val _currentClient = MutableStateFlow(client)
    val currentClient = _currentClient.asStateFlow()

    suspend fun saveClient(): SimpleResult = repository.saveClient(_currentClient.value)

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