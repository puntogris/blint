package com.puntogris.blint.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class Business(
    var businessId: String = "",
    val businessName: String = "",
    val type: String = "",
    val owner: String = "",
    val status: String = "",
    val businessCreatedAt: Timestamp = Timestamp.now(),
    val lastStatusTimestamp: Timestamp = Timestamp.now(),
    val logoUri: String = ""
)