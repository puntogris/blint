package com.puntogris.blint.utils.types

import com.puntogris.blint.model.Event

sealed class EventsDashboard {
    class Success(val data: List<Event>) : EventsDashboard()
    class Error(val exception: Exception) : EventsDashboard()
    object DataNotFound : EventsDashboard()
}