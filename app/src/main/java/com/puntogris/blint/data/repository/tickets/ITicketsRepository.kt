package com.puntogris.blint.data.repository.tickets

import com.puntogris.blint.model.Ticket
import com.puntogris.blint.utils.SimpleResult

interface ITicketsRepository{
    suspend fun sendTicketDatabase(ticket: Ticket): SimpleResult
}