package com.puntogris.blint.feature_store.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.MainAdapterHeaderVhBinding
import com.puntogris.blint.feature_store.domain.model.MenuCard

class HeaderViewHolder private constructor(val binding: MainAdapterHeaderVhBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(menuCard: MenuCard) {
        binding.menuCard = menuCard
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): HeaderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = MainAdapterHeaderVhBinding.inflate(layoutInflater, parent, false)
            return HeaderViewHolder(binding)
        }
    }
}
