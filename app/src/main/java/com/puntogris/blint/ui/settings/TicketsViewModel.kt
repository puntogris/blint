package com.puntogris.blint.ui.settings

import android.text.Editable
import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.repository.tickets.TicketsRepository
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Ticket
import com.puntogris.blint.utils.types.RepoResult
import com.puntogris.blint.utils.types.SimpleRepoResult
import com.puntogris.blint.utils.types.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TicketsViewModel @Inject constructor(
    private val ticketsRepository: TicketsRepository
) : ViewModel() {

    private val ticket = Ticket()

    fun sendTicket(): Flow<SimpleRepoResult> {
        return ticketsRepository.sendTicket(ticket)
    }

    fun updateTicketReason(reason: String) {
        ticket.reason = reason
    }

    fun updateTicketMessage(editable: Editable){
        ticket.message = editable.toString()
    }
}