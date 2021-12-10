package com.puntogris.blint.feature_store.presentation.calendar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.common.utils.toEventUi
import com.puntogris.blint.common.utils.types.EventStatus
import com.puntogris.blint.feature_store.domain.model.Event
import com.puntogris.blint.feature_store.domain.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: EventRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val event = savedStateHandle.getLiveData<Event>("event")
        .asFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Event())

    private val eventFilter = MutableStateFlow(EventStatus.All)

    @OptIn(ExperimentalCoroutinesApi::class)
    val eventsFlow = eventFilter.flatMapLatest {
        repository.getEventsPaged(it)
    }.toEventUi().cachedIn(viewModelScope)

    fun updateEventStatusFilter(eventStatus: EventStatus) {
        this.eventFilter.value = eventStatus
    }

    suspend fun deleteEvent() = repository.deleteEvent(event.value.eventId)

    suspend fun updateEvent() = repository.updateEventStatus(event.value)

    fun updateEventStatus(position: Int) {
        event.value.status =
            if (position == 0) EventStatus.Pending.value else EventStatus.Finished.value
    }
}
