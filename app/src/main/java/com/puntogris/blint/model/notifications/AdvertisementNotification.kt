package com.puntogris.blint.model.notifications

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class AdvertisementNotification(
    val id: String = "",
    val message: String = "",
    val uriToShow: String = "",
    val wasRead: Boolean = true,
    val timestamp: Timestamp = Timestamp.now()
)