package com.puntogris.blint.feature_store.presentation.main

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.feature_store.domain.model.DashboardItem

class DashboardItemDiffCallBack : DiffUtil.ItemCallback<DashboardItem>() {
    override fun areItemsTheSame(oldItem: DashboardItem, newItem: DashboardItem): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: DashboardItem, newItem: DashboardItem): Boolean {
        return oldItem == newItem
    }
}