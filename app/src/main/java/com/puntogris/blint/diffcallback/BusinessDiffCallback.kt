package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.Business

class BusinessDiffCallback : DiffUtil.ItemCallback<Business>() {
    override fun areItemsTheSame(oldItem: Business, newItem: Business): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Business, newItem: Business): Boolean {
        return oldItem == newItem
    }
}