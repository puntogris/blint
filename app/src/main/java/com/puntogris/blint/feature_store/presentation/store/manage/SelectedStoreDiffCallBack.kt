package com.puntogris.blint.feature_store.presentation.store.manage

import androidx.recyclerview.widget.DiffUtil

class SelectedStoreDiffCallBack : DiffUtil.ItemCallback<SelectedStore>() {
    override fun areItemsTheSame(oldItem: SelectedStore, newItem: SelectedStore): Boolean {
        return oldItem.store.storeId == newItem.store.storeId
    }

    override fun areContentsTheSame(oldItem: SelectedStore, newItem: SelectedStore): Boolean {
        return oldItem == newItem
    }
}