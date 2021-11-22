package com.puntogris.blint.data.repository.tickets

import com.puntogris.blint.R
import com.puntogris.blint.data.data_source.remote.FirebaseClients
import com.puntogris.blint.model.Ticket
import com.puntogris.blint.utils.Constants
import com.puntogris.blint.utils.DispatcherProvider
import com.puntogris.blint.utils.types.RepoResult
import com.puntogris.blint.utils.types.SimpleRepoResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TicketsRepository @Inject constructor(
    private val dispatcher: DispatcherProvider,
    private val firebase: FirebaseClients
) : ITicketsRepository {

    override fun sendTicket(ticket: Ticket): Flow<SimpleRepoResult> = flow {
        try {
            emit(RepoResult.InProgress)

            if (!ticket.isValid()) emit(RepoResult.Error(R.string.snack_ticket_missing_required_data))

            firebase.firestore
                .collection(Constants.TICKETS_COLLECTION)
                .document(ticket.ticketId)
                .set(ticket)
                .await()

            emit(RepoResult.Success(Unit))
        } catch (e: Exception) {
            emit(RepoResult.Error(R.string.snack_error_connection_server_try_later))
        }
    }.flowOn(dispatcher.io)
}