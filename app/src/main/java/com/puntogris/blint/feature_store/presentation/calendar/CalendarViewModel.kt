package com.puntogris.blint.feature_store.presentation.calendar

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.google.firebase.Timestamp
import com.puntogris.blint.common.utils.toEventUi
import com.puntogris.blint.common.utils.types.EventStatus
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.feature_store.domain.model.Event
import com.puntogris.blint.feature_store.domain.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: EventRepository
) : ViewModel() {

    private val _event = MutableStateFlow(Event())
    val event: LiveData<Event> = _event.asLiveData()

    fun setEvent(event: Event) {
        _event.value = event
    }

    private var timestamp = Timestamp.now()

    private val eventFilter = MutableStateFlow(EventStatus.All)

    @ExperimentalCoroutinesApi
    val eventsFlow = eventFilter.flatMapLatest {
        repository.getEventsPaged(it).toEventUi()
    }.cachedIn(viewModelScope)

    fun updateEventStatusFilter(eventStatus: EventStatus) {
        this.eventFilter.value = eventStatus
    }

    suspend fun createEvent(title: String, message: String): SimpleResult {
        val event = Event(
            title = title,
            message = message,
            timestamp = timestamp
        )
        return repository.saveEvent(event)
    }

    suspend fun deleteEvent(eventId: String) = repository.deleteEvent(eventId)

    suspend fun updateEvent() = repository.updateEventStatus(_event.value)

    fun updateEventDate(timeInMillis: Long) {
        timestamp = Timestamp(timeInMillis / 1000, 0)
    }

    fun updateEventStatus(position: Int) {
        _event.value.status =
            if (position == 0) EventStatus.Pending.value else EventStatus.Finished.value
    }
}