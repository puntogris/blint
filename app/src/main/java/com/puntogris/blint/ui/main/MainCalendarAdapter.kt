package com.puntogris.blint.ui.main

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.model.Event

class MainCalendarAdapter(private val clickListener: (Event) -> Unit) :
    RecyclerView.Adapter<MainCalendarViewHolder>() {

    private var list = listOf<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainCalendarViewHolder {
        return MainCalendarViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MainCalendarViewHolder, position: Int) {
        holder.bind(list[position], clickListener)
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Event>) {
        this.list = list
        notifyDataSetChanged()
    }
}