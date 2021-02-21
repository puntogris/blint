package com.puntogris.blint.ui.business

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.JoinBusinessVhBinding
import com.puntogris.blint.model.Employee

class JoinBusinessViewHolder private constructor(val binding: JoinBusinessVhBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(employee: Employee) {
        binding.business = employee
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): JoinBusinessViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = JoinBusinessVhBinding.inflate(layoutInflater,parent, false)
            return JoinBusinessViewHolder(binding)
        }
    }
}
