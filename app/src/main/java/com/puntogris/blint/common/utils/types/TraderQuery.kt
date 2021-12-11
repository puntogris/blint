package com.puntogris.blint.common.utils.types

data class TraderQuery(
    val query: String = "",
    val filter: TraderFilter = TraderFilter.All
)

enum class TraderFilter(val type: String) {
    All("ALL"), CLIENT("CLIENT"), SUPPLIER("SUPPLIER")
}