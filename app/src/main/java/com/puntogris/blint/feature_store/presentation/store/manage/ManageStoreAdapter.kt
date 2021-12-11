package com.puntogris.blint.feature_store.presentation.store.manage

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.feature_store.domain.model.Store

class ManageStoreAdapter(
    private val clickListener: (Store) -> Unit,
    private val selectListener: (Store) -> Unit
) :
    ListAdapter<SelectedStore, ManageStoreViewHolder>(SelectedStoreDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageStoreViewHolder {
        return ManageStoreViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageStoreViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, selectListener)
    }
}

