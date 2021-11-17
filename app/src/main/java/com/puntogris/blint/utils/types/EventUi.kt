package com.puntogris.blint.utils.types

import com.puntogris.blint.model.Event

sealed class EventUi {
    data class EventItem(val event: Event) : EventUi()
    data class SeparatorItem(val date: String) : EventUi()
}