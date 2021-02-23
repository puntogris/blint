package com.puntogris.blint.model.notifications

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class EmploymentRequestReceivedNotif(
    val id:String = "",
    val employeeEmail: String = "",
    val wasRead: Boolean = true,
    val timestamp: Timestamp = Timestamp.now()
)