package com.puntogris.blint.ui.calendar

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.local.dao.EventsDao
import com.puntogris.blint.model.Event
import kotlinx.coroutines.flow.Flow

class CalendarViewModel @ViewModelInject constructor(
    private val eventsDao: EventsDao
):ViewModel() {

    fun getEvents(): Flow<PagingData<Event>> {
        return Pager(
            PagingConfig(
            pageSize = 30,
            enablePlaceholders = true,
            maxSize = 200
        )
        ){
            eventsDao.getAllPaged()
        }.flow
    }
}