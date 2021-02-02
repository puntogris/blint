package com.puntogris.blint.ui.calendar

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.EventDiffCallBack
import com.puntogris.blint.model.Event
import com.puntogris.blint.ui.product.ManageProductsViewHolder

class CalendarEventsAdapter(private val clickListener: (Event) -> Unit): PagingDataAdapter<Event, CalendarEventsViewHolder>(
    EventDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarEventsViewHolder {
        return CalendarEventsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CalendarEventsViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

}