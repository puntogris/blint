package com.puntogris.blint.feature_store.presentation.business.manage

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.feature_store.domain.model.Business
import com.puntogris.blint.feature_store.presentation.business.BusinessDiffCallBack

class ManageBusinessAdapter(private val clickListener: (Business) -> Unit) :
    ListAdapter<Business, ManageBusinessViewHolder>(BusinessDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageBusinessViewHolder {
        return ManageBusinessViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageBusinessViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}