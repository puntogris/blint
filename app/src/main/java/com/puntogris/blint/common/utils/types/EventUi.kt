package com.puntogris.blint.common.utils.types

import com.puntogris.blint.feature_store.domain.model.Event

sealed class EventUi {
    data class EventItem(val event: Event) : EventUi()
    data class SeparatorItem(val date: String) : EventUi()
}