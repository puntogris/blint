package com.puntogris.blint.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.Timestamp
import com.puntogris.blint.data.repository.events.EventRepository
import com.puntogris.blint.model.Event
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.toEventUiFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val eventRepository: EventRepository
):ViewModel() {

    private val _event = MutableStateFlow(Event())
    val event:LiveData<Event> = _event.asLiveData()

    fun setEvent(event: Event){
        _event.value = event
    }

    private var timestamp = Timestamp.now()

    private val eventsFilter = MutableStateFlow("ALL")

    fun setPendingFilter(){
        eventsFilter.value = "PENDING"
    }

    fun setFinishedFilter(){
        eventsFilter.value = "FINISHED"
    }

    fun setAllFilter(){
        eventsFilter.value = "ALL"
    }

    @ExperimentalCoroutinesApi
    val events = eventsFilter.flatMapLatest {
        eventRepository.getEventPagingDataFlow(it).toEventUiFlow()
    }

    suspend fun createEvent(title:String, message:String): SimpleResult{
        val event = Event(
            title = title,
            message = message,
            timestamp = timestamp)
        return eventRepository.createEventDatabase(event)
    }

    suspend fun deleteEvent(eventId: String) = eventRepository.deleteEventDatabase(eventId)

    suspend fun updateEvent() = eventRepository.updateEventStatusDatabase(_event.value)

    fun updateEventDate(timeInMillis:Long){
        timestamp = Timestamp(timeInMillis / 1000,0)
    }

    fun updateEventStatus(position:Int){
        _event.value.status = if (position == 0) "PENDING" else "FINISHED"
    }

//    fun getDayEvents(date:Date){
//        val flow = Pager(
//            PagingConfig(
//                pageSize = 30,
//                enablePlaceholders = true,
//                maxSize = 200
//            )
//        ){
//            eventsDao.getDayEvents(Timestamp(1613691993,0))
//        }.flow.toEventUiFlow()
//        viewModelScope.launch {
//            _events.emitAll(flow)
//
//        }
//    }

}