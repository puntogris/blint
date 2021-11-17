package com.puntogris.blint.ui.main

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.MainCalendarDiffCallBack
import com.puntogris.blint.model.Event

class MainCalendarAdapter(private val clickListener: (Event) -> Unit) :
    ListAdapter<Event, MainCalendarViewHolder>(
        MainCalendarDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainCalendarViewHolder {
        return MainCalendarViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MainCalendarViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}