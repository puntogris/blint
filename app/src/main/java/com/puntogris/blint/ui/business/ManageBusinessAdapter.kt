package com.puntogris.blint.ui.business

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.BusinessDiffCallback
import com.puntogris.blint.model.Employee

class ManageBusinessAdapter(private val clickListener: (Employee) -> Unit): ListAdapter<Employee, ManageBusinessViewHolder>(BusinessDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageBusinessViewHolder {
        return ManageBusinessViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageBusinessViewHolder, position: Int) {
        holder.bind(getItem(position),clickListener)
    }


}