package com.puntogris.blint.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
@Keep
class Notification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val navigationUri: String = "",
    val wasRead: Boolean = true,
    val imageUri: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val type: String = ""
)