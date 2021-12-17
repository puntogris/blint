package com.puntogris.blint.feature_store.presentation.events.new_event

import androidx.lifecycle.ViewModel
import com.puntogris.blint.feature_store.domain.model.Event
import com.puntogris.blint.feature_store.domain.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {

    private val _event = MutableStateFlow(Event())
    val event = _event.asStateFlow()

    suspend fun createEvent() = repository.saveEvent(event.value)

    fun setEventContent(content: String){
        _event.value = _event.value.copy(
            content = content
        )
    }

    fun setEventDate(year: Int, month: Int, day: Int) {
        _event.value = _event.value.copy(
            timestamp = OffsetDateTime.of(year, month + 1, day, 0, 0, 0, 0, ZoneOffset.UTC)
        )
    }
}