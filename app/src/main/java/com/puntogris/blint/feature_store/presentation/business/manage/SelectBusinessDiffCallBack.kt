package com.puntogris.blint.feature_store.presentation.business.manage

import androidx.recyclerview.widget.DiffUtil

class SelectBusinessDiffCallBack : DiffUtil.ItemCallback<SelectedBusiness>() {
    override fun areItemsTheSame(oldItem: SelectedBusiness, newItem: SelectedBusiness): Boolean {
        return oldItem.business.businessId == newItem.business.businessId
    }

    override fun areContentsTheSame(oldItem: SelectedBusiness, newItem: SelectedBusiness): Boolean {
        return oldItem == newItem
    }
}