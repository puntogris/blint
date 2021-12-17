package com.puntogris.blint.feature_store.presentation.events.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.CalendarSeparatorVhBinding

class EventsDividerViewHolder private constructor(val binding: CalendarSeparatorVhBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(date: String) {
        binding.date = date
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): EventsDividerViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = CalendarSeparatorVhBinding.inflate(layoutInflater, parent, false)
            return EventsDividerViewHolder(binding)
        }
    }
}
