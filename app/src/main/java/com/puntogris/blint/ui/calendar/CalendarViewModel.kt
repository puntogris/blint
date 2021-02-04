package com.puntogris.blint.ui.calendar

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.google.firebase.Timestamp
import com.puntogris.blint.data.local.dao.EventsDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.model.Event
import com.puntogris.blint.utils.EventUi
import com.puntogris.blint.utils.getDateFormattedString
import com.puntogris.blint.utils.getMonthAndYeah
import com.puntogris.blint.utils.toEventUiFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.time.seconds

class CalendarViewModel @ViewModelInject constructor(
    private val eventsDao: EventsDao,
    private val usersDao: UsersDao
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
        Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ){
            if (it == "ALL") eventsDao.getAllPaged()
            else eventsDao.getPagedEventsWithFilter(it)

        }.flow.toEventUiFlow().cachedIn(viewModelScope)
    }

    suspend fun createEvent(title:String, message:String){
        eventsDao.insert(Event(
            title = title,
            message = message,
            timestamp = timestamp,
            businessId = usersDao.getUser().currentBusinessId))
    }

    fun updateEventDate(timeInMillis:Long){
        timestamp = Timestamp(timeInMillis / 1000,0)
    }

    fun updateEventStatus(position:Int){
        _event.value.status = if (position == 0) "PENDING" else "FINISHED"
    }

    suspend fun saveEvent(){
        eventsDao.updateEvent(_event.value)
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