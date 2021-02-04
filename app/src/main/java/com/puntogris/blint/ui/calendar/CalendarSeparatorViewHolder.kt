package com.puntogris.blint.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.CalendarSeparatorVhBinding

class CalendarSeparatorViewHolder private constructor(val binding: CalendarSeparatorVhBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(date: String) {
        binding.date = date
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): CalendarSeparatorViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = CalendarSeparatorVhBinding.inflate(layoutInflater,parent, false)
            return CalendarSeparatorViewHolder(binding)
        }
    }
}
