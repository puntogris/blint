package com.puntogris.blint.common.utils.types

import com.puntogris.blint.feature_store.domain.model.Event

sealed class EventsDashboard {
    class Success(val data: List<Event>) : EventsDashboard()
    class Error(val exception: Exception) : EventsDashboard()
    object DataNotFound : EventsDashboard()
}