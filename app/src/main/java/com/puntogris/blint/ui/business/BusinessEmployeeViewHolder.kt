package com.puntogris.blint.ui.business

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.BusinessEmployeeVhBinding
import com.puntogris.blint.model.Business

class BusinessEmployeeViewHolder private constructor(val binding: BusinessEmployeeVhBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(business: Business, clickListener:(Business)->(Unit)) {
        binding.employee = business
        binding.root.setOnClickListener { clickListener(business) }
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
