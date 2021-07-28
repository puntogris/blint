package com.puntogris.blint.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class UserData(val name: String = "", val country: String = ""):Parcelable{

    fun dataMissing(): Boolean = (name.isBlank() || country.isBlank())
}
