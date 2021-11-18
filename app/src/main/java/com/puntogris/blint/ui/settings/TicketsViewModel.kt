package com.puntogris.blint.ui.settings

import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.repository.tickets.TicketsRepository
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Ticket
import com.puntogris.blint.utils.types.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TicketsViewModel @Inject constructor(
    private val ticketsRepository: TicketsRepository
) : ViewModel() {

    private val ticket = Ticket()

    suspend fun sendTicket(message: String): SimpleResult {
        ticket.message = message
        return ticketsRepository.sendTicket(ticket)
    }

    fun updateTicketBusiness(business: Business) {
        ticket.apply {
            businessStatus = business.status
        }
    }
}