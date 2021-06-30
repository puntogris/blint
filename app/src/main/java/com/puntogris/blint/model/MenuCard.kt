package com.puntogris.blint.model

import androidx.annotation.DrawableRes

data class MenuCard(
    val code: Int,
    val title: String,
    val navigationId: Int,
    @DrawableRes val icon: Int)