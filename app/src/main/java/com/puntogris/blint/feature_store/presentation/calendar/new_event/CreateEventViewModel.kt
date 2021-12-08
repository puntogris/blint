package com.puntogris.blint.feature_store.presentation.calendar.new_event

import android.text.Editable
import androidx.lifecycle.ViewModel
import com.puntogris.blint.common.utils.toOffsetDateTime
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.domain.model.Event
import com.puntogris.blint.feature_store.domain.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {

    private val _event = MutableStateFlow(Event())
    val event = _event.asStateFlow()

    suspend fun createEvent(): SimpleResult = repository.saveEvent(event.value)

    fun setEventTitle(editable: Editable) {
        event.value.title = editable.toString()
    }

    fun setEventMessage(editable: Editable) {
        event.value.message = editable.toString()
    }

    fun setEventDate(calendar: Calendar) {
        _event.value = _event.value.copy(
            timestamp = calendar.toOffsetDateTime()
        )
    }
}