package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.MenuCard

class MenuCardDiffCallback : DiffUtil.ItemCallback<MenuCard>() {
    override fun areItemsTheSame(oldItem: MenuCard, newItem: MenuCard): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: MenuCard, newItem: MenuCard): Boolean {
        return oldItem == newItem
    }
}