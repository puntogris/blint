package com.puntogris.blint.feature_store.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.MenuCardVhBinding
import com.puntogris.blint.feature_store.domain.model.MenuCard

class MenuCardViewHolder private constructor(val binding: MenuCardVhBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(menuCard: MenuCard, clickListener: (MenuCard) -> Unit) {
        binding.menuCard = menuCard
        binding.menuCardLayout.setOnClickListener { clickListener(menuCard) }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): MenuCardViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = MenuCardVhBinding.inflate(layoutInflater, parent, false)
            return MenuCardViewHolder(binding)
        }
    }
}
