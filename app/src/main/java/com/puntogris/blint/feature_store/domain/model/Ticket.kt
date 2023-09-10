package com.puntogris.blint.feature_store.domain.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.puntogris.blint.common.utils.UUIDGenerator

@Keep
data class Ticket(
    var ticketId: String = UUIDGenerator.randomUUID(),
    var message: String = "",
    var timestamp: Timestamp = Timestamp.now(),
    var reason: String = ""
) {
    fun isValid() = message.isNotBlank() && reason.isNotBlank()
}
