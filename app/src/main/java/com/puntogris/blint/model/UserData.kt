package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserData(val name: String = "", val country: String = ""):Parcelable{

    fun dataMissing(): Boolean = (name.isBlank() || country.isBlank())
}