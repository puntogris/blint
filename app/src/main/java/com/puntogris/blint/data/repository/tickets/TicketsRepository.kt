package com.puntogris.blint.data.repository.tickets

import com.puntogris.blint.data.data_source.remote.FirebaseClients
import com.puntogris.blint.model.Ticket
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.SimpleResult
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TicketsRepository @Inject constructor(
    private val dispatcher: DispatcherProvider,
    private val firebase: FirebaseClients
) : ITicketsRepository {


    override suspend fun sendTicket(ticket: Ticket): SimpleResult =
        withContext(dispatcher.io) {
            try {
                val ref = firebase.firestore.collection("tickets").document()
                ticket.ticketId = ref.id
                ref.set(ticket).await()

                SimpleResult.Success
            } catch (e: Exception) {
                SimpleResult.Failure
            }
        }
}