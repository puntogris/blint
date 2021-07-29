package com.puntogris.blint.ui.business

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.EmployeeDiffCallBack
import com.puntogris.blint.model.Employee

class BusinessEmployeeAdapter(private val clickListener: (Employee)->(Unit)): ListAdapter<Employee, BusinessEmployeeViewHolder>(EmployeeDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessEmployeeViewHolder {
        return BusinessEmployeeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BusinessEmployeeViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}