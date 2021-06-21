package com.puntogris.blint.model

import androidx.annotation.DrawableRes
import com.google.firebase.Timestamp
import com.puntogris.blint.R

data class BusinessItem(
    var businessId: String = "",
    val businessName: String = "",
    val businessType: String = "",
    @DrawableRes val avatar: Int,
    var isCurrentAccount: Boolean = false
)