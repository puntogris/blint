package com.puntogris.blint.feature_store.presentation.main

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.feature_store.domain.model.MenuCard

class MenuCardDiffCallback : DiffUtil.ItemCallback<MenuCard>() {
    override fun areItemsTheSame(oldItem: MenuCard, newItem: MenuCard): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: MenuCard, newItem: MenuCard): Boolean {
        return oldItem == newItem
    }
}