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
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class EditTraderViewModel @Inject constructor(
    private val repository: TraderRepository,
    private val savedStateHandle: SavedStateHandle,
    private val contactsHelper: ContactsHelper
) : ViewModel() {

    val currentTrader = savedStateHandle.getLiveData<Trader>("trader")
        .asFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Trader())

    suspend fun saveClient() = repository.saveTrader(currentTrader.value)

    fun updateTraderName(editable: Editable) {
        currentTrader.value.name = editable.toString()
    }

    fun updateTraderAddress(editable: Editable) {
        currentTrader.value.address = editable.toString()
    }

    fun updateTraderEmail(editable: Editable) {
        currentTrader.value.email = editable.toString()
    }

    fun updateTraderPhone(editable: Editable) {
        currentTrader.value.phone = editable.toString()
    }

    fun updateTraderPaymentInfo(editable: Editable) {
        currentTrader.value.paymentInfo = editable.toString()
    }

    fun updateTraderNotes(editable: Editable) {
        currentTrader.value.notes = editable.toString()
    }

    fun updateTraderType(type: String) {
        currentTrader.value.type = type
    }

    fun setContact(uri: Uri) {
        contactsHelper.getContact(uri)?.let {
            savedStateHandle.set(
                "trader",
                currentTrader.value.copy(
                    name = it.name,
                    email = it.email,
                    address = it.address,
                    phone = it.phone
                )
            )
        }
    }
}