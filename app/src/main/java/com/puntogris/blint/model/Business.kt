package com.puntogris.blint.model

import com.google.firebase.Timestamp

data class Business(
    var businessId: String = "",
    val businessName: String = "",
    val type: String = "",
    val owner: String = "",
    val valid: Boolean = true,
    val creationTimestamp: Timestamp = Timestamp.now()
)
