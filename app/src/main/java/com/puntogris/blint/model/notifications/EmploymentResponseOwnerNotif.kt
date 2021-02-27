package com.puntogris.blint.model.notifications

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
class EmploymentResponseOwnerNotif(
    val id:String = "",
    val employeeEmail: String = "",
    val wasRead: Boolean = true,
    val type: String = "",
    val timestamp: Timestamp = Timestamp.now()
):Parcelable