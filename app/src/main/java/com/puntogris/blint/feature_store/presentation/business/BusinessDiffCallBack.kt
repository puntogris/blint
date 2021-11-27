package com.puntogris.blint.feature_store.presentation.business

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.feature_store.domain.model.Business

class BusinessDiffCallBack : DiffUtil.ItemCallback<Business>() {
    override fun areItemsTheSame(oldItem: Business, newItem: Business): Boolean {
        return oldItem.businessId == newItem.businessId
    }

    override fun areContentsTheSame(oldItem: Business, newItem: Business): Boolean {
        return oldItem == newItem
    }
}