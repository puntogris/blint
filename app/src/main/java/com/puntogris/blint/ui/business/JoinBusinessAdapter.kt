package com.puntogris.blint.ui.business

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.EmployeeDiffCallBack
import com.puntogris.blint.model.Employee

class JoinBusinessAdapter: ListAdapter<Employee, JoinBusinessViewHolder>(EmployeeDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JoinBusinessViewHolder {
       return JoinBusinessViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: JoinBusinessViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}