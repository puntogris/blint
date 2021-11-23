package com.puntogris.blint.data.repository.tickets

import com.puntogris.blint.model.Ticket
import com.puntogris.blint.utils.types.SimpleRepoResult
import kotlinx.coroutines.flow.Flow

interface ITicketsRepository {
    fun sendTicket(ticket: Ticket): Flow<SimpleRepoResult>
}