package com.puntogris.blint.ui.debt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.DebtPagingVhBinding
import com.puntogris.blint.model.Debt

class DebtPagingViewHolder private constructor(val binding: DebtPagingVhBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(debt: Debt, clickListener:(Debt)->(Unit)) {
        binding.debt = debt
        binding.root.setOnClickListener { clickListener(debt) }
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): DebtPagingViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DebtPagingVhBinding.inflate(layoutInflater,parent, false)
            return DebtPagingViewHolder(binding)
        }
    }
}
