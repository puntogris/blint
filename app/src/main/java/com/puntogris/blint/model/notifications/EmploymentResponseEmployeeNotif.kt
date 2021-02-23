package com.puntogris.blint.model.notifications

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class EmploymentResponseEmployeeNotif(
    val id: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val businessName:String ="",
    val wasRead: Boolean = true,
)