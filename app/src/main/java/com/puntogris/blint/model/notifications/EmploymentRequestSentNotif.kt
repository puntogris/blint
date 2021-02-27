package com.puntogris.blint.model.notifications

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties

class EmploymentRequestSentNotif (
    val id: String = "",
    val employeeEmail: String = "",
    val type: String = "",
    val wasRead: Boolean = true,
    val businessName: String = "",
    val timestamp: Timestamp = Timestamp.now()
    )