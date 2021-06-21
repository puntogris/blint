package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.BusinessItem

class BusinessItemDiffCallBack : DiffUtil.ItemCallback<BusinessItem>() {
    override fun areItemsTheSame(oldItem: BusinessItem, newItem: BusinessItem): Boolean {
        return oldItem.businessId == newItem.businessId
    }

    override fun areContentsTheSame(oldItem: BusinessItem, newItem: BusinessItem): Boolean {
        return oldItem == newItem
    }
}