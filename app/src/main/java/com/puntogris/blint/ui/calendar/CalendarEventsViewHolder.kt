package com.puntogris.blint.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.CalendarEventVhBinding
import com.puntogris.blint.model.Event

class CalendarEventsViewHolder private constructor(val binding: CalendarEventVhBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(event: Event, clickListener: (Event) -> Unit) {
        binding.event = event
        binding.root.setOnClickListener { clickListener(event) }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): CalendarEventsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = CalendarEventVhBinding.inflate(layoutInflater, parent, false)
            return CalendarEventsViewHolder(binding)
        }
    }
}
