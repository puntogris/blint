package com.puntogris.blint.feature_store.presentation.events.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.types.EventUi
import com.puntogris.blint.feature_store.domain.model.Event
import com.puntogris.blint.feature_store.presentation.events.EventDiffCallBack

class ManageEventsAdapter(private val clickListener: (Event, Int) -> Unit) :
    PagingDataAdapter<EventUi, RecyclerView.ViewHolder>(EventDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.calendar_event_vh -> EventItemViewHolder.from(parent)
            else -> EventsDividerViewHolder.from(parent)
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
                    (holder as EventItemViewHolder).bind(it.event, clickListener, position)
                }
                is EventUi.SeparatorItem -> {
                    (holder as EventsDividerViewHolder).bind(it.date)
                }
            }
        }
    }
}