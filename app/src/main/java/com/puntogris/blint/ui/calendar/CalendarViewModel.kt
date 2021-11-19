package com.puntogris.blint.ui.calendar

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.google.firebase.Timestamp
import com.puntogris.blint.data.repository.events.EventRepository
import com.puntogris.blint.model.Event
import com.puntogris.blint.utils.toEventUiFlow
import com.puntogris.blint.utils.types.EventStatus
import com.puntogris.blint.utils.types.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val eventFilter = MutableLiveData(EventStatus.All)

    val eventsLiveData = eventFilter.switchMap {
        repository.getEventsPaged(it).toEventUiFlow().asLiveData()
    }.cachedIn(viewModelScope)

    fun updateEventStatusFilter(eventStatus: EventStatus){
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

    suspend fun deleteEvent(eventId: Int) = repository.deleteEvent(eventId)

    suspend fun updateEvent() = repository.updateEventStatus(_event.value)

    fun updateEventDate(timeInMillis: Long) {
        timestamp = Timestamp(timeInMillis / 1000, 0)
    }

    fun updateEventStatus(position: Int) {
        _event.value.status = if (position == 0) EventStatus.Pending.value else EventStatus.Finished.value
    }
}