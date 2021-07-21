package com.puntogris.blint.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class JoinCode(
    val codeId: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val businessId: String = "",
    val ownerId: String = ""
)