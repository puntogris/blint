package com.puntogris.blint.model

import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class UserData(val name: String = "", val country: String = ""):Parcelable{

    fun dataMissing(): Boolean = (name.isBlank() || country.isBlank())
}