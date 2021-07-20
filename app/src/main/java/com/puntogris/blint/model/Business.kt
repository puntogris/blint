package com.puntogris.blint.model

import com.google.firebase.Timestamp

data class Business(
    var businessId: String = "",
    val businessName: String = "",
    val type: String = "",
    val owner: String = "",
    val status: String = "",
    val businessCreatedAt: Timestamp = Timestamp.now(),
    val lastStatusTimestamp: Timestamp = Timestamp.now()
)