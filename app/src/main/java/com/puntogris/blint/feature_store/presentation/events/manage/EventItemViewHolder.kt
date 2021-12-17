package com.puntogris.blint.feature_store.presentation.events.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.CalendarEventVhBinding
import com.puntogris.blint.feature_store.domain.model.Event

class EventItemViewHolder private constructor(val binding: CalendarEventVhBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(event: Event, clickListener: (Event, Int) -> Unit, position: Int) {
        binding.event = event
        binding.root.setOnClickListener { clickListener(event, position) }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): EventItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = CalendarEventVhBinding.inflate(layoutInflater, parent, false)
            return EventItemViewHolder(binding)
        }
    }
}
