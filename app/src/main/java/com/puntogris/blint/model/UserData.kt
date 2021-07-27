package com.puntogris.blint.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserData(val name: String = "", val country: String = ""):Parcelable{

    fun dataMissing(): Boolean = (name.isBlank() || country.isBlank())
}
