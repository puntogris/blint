package com.puntogris.blint.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.CalendarEventVhBinding
import com.puntogris.blint.model.Event

class MainCalendarViewHolder private constructor(val binding: CalendarEventVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(event: Event, clickListener: (Event) -> Unit) {
        binding.event = event
        binding.root.setOnClickListener { clickListener(event) }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): MainCalendarViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = CalendarEventVhBinding.inflate(layoutInflater, parent, false)
            return MainCalendarViewHolder(binding)
        }
    }
}
