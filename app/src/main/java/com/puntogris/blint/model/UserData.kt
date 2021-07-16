package com.puntogris.blint.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserData(val username: String, val country: String):Parcelable