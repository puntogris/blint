package com.puntogris.blint.ui.client.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.ManageClientsDiffCallBack
import com.puntogris.blint.model.Client

class ManageClientsAdapter(private val clickListener: (Client) -> Unit) :
    PagingDataAdapter<Client, ManageClientViewHolder>(
        ManageClientsDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageClientViewHolder {
        return ManageClientViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageClientViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}