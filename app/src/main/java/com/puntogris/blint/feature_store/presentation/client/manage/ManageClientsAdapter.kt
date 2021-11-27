package com.puntogris.blint.feature_store.presentation.client.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.feature_store.domain.model.Client
import com.puntogris.blint.feature_store.presentation.client.ClientDiffCallBack

class ManageClientsAdapter(private val clickListener: (Client) -> Unit) :
    PagingDataAdapter<Client, ManageClientViewHolder>(
        ClientDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageClientViewHolder {
        return ManageClientViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageClientViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}