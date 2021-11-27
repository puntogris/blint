package com.puntogris.blint.feature_store.presentation.business.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.BusinessItemVhBinding
import com.puntogris.blint.feature_store.domain.model.Business

class ManageBusinessViewHolder private constructor(val binding: BusinessItemVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(business: Business, clickListener: (Business) -> (Unit)) {
        binding.business = business
        binding.root.setOnClickListener { clickListener(business) }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ManageBusinessViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = BusinessItemVhBinding.inflate(layoutInflater, parent, false)
            return ManageBusinessViewHolder(binding)
        }
    }
}
