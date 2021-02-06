package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.DashboardItem

class DashboardItemDiffCallBack : DiffUtil.ItemCallback<DashboardItem>() {
    override fun areItemsTheSame(oldItem: DashboardItem, newItem: DashboardItem): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: DashboardItem, newItem: DashboardItem): Boolean {
        return oldItem == newItem
    }
}