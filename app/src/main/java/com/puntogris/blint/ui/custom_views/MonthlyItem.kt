package com.puntogris.blint.ui.custom_views

import kotlin.random.Random

data class MonthlyItem(
    val month: Int,
    val name: String,
    val amount: Float,
    val type: ItemType
) {
    val date get() = "$month/${Random.nextInt(30)}/2018"
}

enum class ItemType {
    INCREASE,
    DECREASE
}

