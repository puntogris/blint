package com.puntogris.blint.model

import androidx.annotation.DrawableRes

data class BusinessItem(
    var businessId: String = "",
    val businessName: String = "",
    val businessType: String = "",
    val businessOwner: String = "",
    @DrawableRes val avatar: Int,
    var isCurrentAccount: Boolean = false,
    val businessStatus: String = ""
)