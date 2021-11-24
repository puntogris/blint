package com.puntogris.blint.ui.settings

import android.text.Editable
import androidx.lifecycle.ViewModel
import com.puntogris.blint.domain.repository.UserRepository
import com.puntogris.blint.model.Ticket
import com.puntogris.blint.utils.types.SimpleRepoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TicketsViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val ticket = Ticket()

    fun sendTicket(): Flow<SimpleRepoResult> = repository.sendTicket(ticket)

    fun updateTicketReason(reason: String) {
        ticket.reason = reason
    }

    fun updateTicketMessage(editable: Editable) {
        ticket.message = editable.toString()
    }
}