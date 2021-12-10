package com.puntogris.blint.feature_store.presentation.settings

import android.text.Editable
import androidx.lifecycle.ViewModel
import com.puntogris.blint.feature_store.domain.model.Ticket
import com.puntogris.blint.feature_store.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TicketsViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val ticket = Ticket()

    fun sendTicket() = repository.sendTicket(ticket)

    fun updateTicketReason(reason: String) {
        ticket.reason = reason
    }

    fun updateTicketMessage(editable: Editable) {
        ticket.message = editable.toString()
    }
}