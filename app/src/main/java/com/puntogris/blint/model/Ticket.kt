package com.puntogris.blint.model

import com.google.firebase.Timestamp

class Ticket(
    var ticketId: String = "",
    var businessId: String = "",
    var message:String = "",
    var employeeId: String = "",
    var timestamp: Timestamp = Timestamp.now(),
    var businessType: String = "",
    var businessStatus: String = "",
    var businessOwner: String = ""
)