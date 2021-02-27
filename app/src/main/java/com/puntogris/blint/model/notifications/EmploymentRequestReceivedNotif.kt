package com.puntogris.blint.model.notifications

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
class EmploymentRequestReceivedNotif(
    val id:String = "",
    val businessName: String = "",
    val type: String = "",
    val businessId: String = "",
    val wasRead: Boolean = true,
    val parentRequestId: String = "",
    val timestamp: Timestamp = Timestamp.now()
):Parcelable