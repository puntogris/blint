package com.puntogris.blint.feature_store.presentation.trader.create_edit

import android.net.Uri
import android.text.Editable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.puntogris.blint.common.framework.ContactsHelper
import com.puntogris.blint.feature_store.domain.model.Trader
import com.puntogris.blint.feature_store.domain.repository.TraderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EditClientViewModel @Inject constructor(
    private val repository: TraderRepository,
    private val savedStateHandle: SavedStateHandle,
    private val contactsHelper: ContactsHelper
) : ViewModel() {

    private val _currentTrader = savedStateHandle.getLiveData<Trader>("trader")
        .asFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Trader())

    val currentTrader: StateFlow<Trader> = _currentTrader

    suspend fun saveClient() = repository.saveTrader(_currentTrader.value)

    fun updateTraderName(editable: Editable) {
        _currentTrader.value.name = editable.toString()
    }

    fun updateTraderAddress(editable: Editable) {
        _currentTrader.value.address = editable.toString()
    }

    fun updateTraderEmail(editable: Editable) {
        _currentTrader.value.email = editable.toString()
    }

    fun updateTraderPhone(editable: Editable) {
        _currentTrader.value.phone = editable.toString()
    }

    fun updateTraderPaymentInfo(editable: Editable) {
        _currentTrader.value.paymentInfo = editable.toString()
    }

    fun updateTraderDiscount(editable: Editable) {
        _currentTrader.value.discount = editable.toString().toFloatOrNull() ?: 0F
    }

    fun updateTraderNotes(editable: Editable) {
        _currentTrader.value.notes = editable.toString()
    }

    fun setContact(uri: Uri) {
        contactsHelper.getContact(uri)?.let {
            savedStateHandle.set(
                "trader",
                _currentTrader.value.copy(
                    name = it.name,
                    email = it.email,
                    address = it.address,
                    phone = it.phone
                )
            )
        }
    }
}