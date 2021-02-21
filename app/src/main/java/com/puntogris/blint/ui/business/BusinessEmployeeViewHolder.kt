package com.puntogris.blint.ui.business

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.BusinessEmployeeVhBinding
import com.puntogris.blint.model.Employee

class BusinessEmployeeViewHolder private constructor(val binding: BusinessEmployeeVhBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(employee: Employee, clickListener:(Employee)->(Unit)) {
        binding.employee = employee
        binding.root.setOnClickListener { clickListener(employee) }
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): BusinessEmployeeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = BusinessEmployeeVhBinding.inflate(layoutInflater,parent, false)
            return BusinessEmployeeViewHolder(binding)
        }
    }
}
