package com.puntogris.blint.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.puntogris.blint.utils.UUIDGenerator

@Keep
class Ticket(
    var ticketId: String = UUIDGenerator.randomUUID(),
    var message: String = "",
    var timestamp: Timestamp = Timestamp.now(),
    var reason: String = ""
) {
    fun isValid() = message.isNotBlank() && reason.isNotBlank()
}