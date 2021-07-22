package com.puntogris.blint.data.repo.tickets

import com.puntogris.blint.model.Ticket
import com.puntogris.blint.utils.SimpleResult

interface ITicketsRepository{
    suspend fun sendTicketDatabase(ticket: Ticket): SimpleResult
}