package com.puntogris.blint.feature_store.presentation.calendar

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.types.EventUi
import com.puntogris.blint.feature_store.domain.model.Event

class CalendarEventsAdapter(private val clickListener: (Event, Int) -> Unit) :
    PagingDataAdapter<EventUi, RecyclerView.ViewHolder>(EventDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.calendar_event_vh -> CalendarEventsViewHolder.from(parent)
            else -> CalendarSeparatorViewHolder.from(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is EventUi.EventItem -> R.layout.calendar_event_vh
            is EventUi.SeparatorItem -> R.layout.calendar_separator_vh
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            when (it) {
                is EventUi.EventItem -> {
                    (holder as CalendarEventsViewHolder).bind(it.event, clickListener, position)
                }
                is EventUi.SeparatorItem -> {
                    (holder as CalendarSeparatorViewHolder).bind(it.date)
                }
            }
        }
    }
}