package com.puntogris.blint.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
class Ticket(
    var ticketId: String = "",
    var message: String = "",
    var timestamp: Timestamp = Timestamp.now(),
    var businessStatus: String = "",
)