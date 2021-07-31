package com.puntogris.blint.ui.business.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.BusinessItemVhBinding
import com.puntogris.blint.model.Employee

class ManageBusinessViewHolder private constructor(val binding: BusinessItemVhBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(employee: Employee, clickListener: (Employee)-> (Unit)) {
        binding.business = employee
        binding.root.setOnClickListener { clickListener(employee) }
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): ManageBusinessViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = BusinessItemVhBinding.inflate(layoutInflater,parent, false)
            return ManageBusinessViewHolder(binding)
        }
    }
}
