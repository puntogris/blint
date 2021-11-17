package com.puntogris.blint.model

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes

data class MenuCard(
    @StringRes val title: Int,
    @IdRes val navigationId: Int,
    @DrawableRes val icon: Int
)
