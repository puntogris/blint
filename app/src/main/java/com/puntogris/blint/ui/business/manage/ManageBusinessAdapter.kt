package com.puntogris.blint.ui.business.manage

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.EmployeeDiffCallBack
import com.puntogris.blint.model.Business

class ManageBusinessAdapter(private val clickListener: (Business) -> Unit): ListAdapter<Business, ManageBusinessViewHolder>(EmployeeDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageBusinessViewHolder {
        return ManageBusinessViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageBusinessViewHolder, position: Int) {
        holder.bind(getItem(position),clickListener)
    }
}