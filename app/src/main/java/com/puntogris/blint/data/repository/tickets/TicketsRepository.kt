package com.puntogris.blint.data.repository.tickets

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.model.Ticket
import com.puntogris.blint.utils.SimpleResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TicketsRepository @Inject constructor(): ITicketsRepository {

    private val firestore = Firebase.firestore

    override suspend fun sendTicketDatabase(ticket: Ticket): SimpleResult = withContext(Dispatchers.IO){
        try {
            val ref = firestore.collection("tickets").document()
            ticket.ticketId = ref.id
            ref.set(ticket).await()

            SimpleResult.Success
        }catch (e:Exception){
            SimpleResult.Failure
        }
    }
}