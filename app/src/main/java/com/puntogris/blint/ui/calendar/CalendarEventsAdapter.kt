package com.puntogris.blint.ui.calendar

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.R
import com.puntogris.blint.diffcallback.EventDiffCallBack
import com.puntogris.blint.model.Event
import com.puntogris.blint.utils.EventUi

class CalendarEventsAdapter(private val clickListener: (Event) -> Unit): PagingDataAdapter<EventUi, RecyclerView.ViewHolder>(
    EventDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            R.layout.calendar_event_vh -> CalendarEventsViewHolder.from(parent)
            else -> CalendarSeparatorViewHolder.from(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is EventUi.EventItem -> R.layout.calendar_event_vh
            is EventUi.SeparatorItem -> R.layout.calendar_separator_vh
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val eventUi = getItem(position)!!

        eventUi.let {
            when(eventUi){
                is EventUi.EventItem ->{
                    (holder as CalendarEventsViewHolder)
                    holder.bind(eventUi.event, clickListener)
                }
                is EventUi.SeparatorItem -> {
                    (holder as CalendarSeparatorViewHolder)
                    holder.bind(eventUi.date)
                }
            }
        }


    }

}