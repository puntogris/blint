package com.puntogris.blint.feature_store.presentation.client

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.feature_store.domain.model.Client

class ClientDiffCallBack : DiffUtil.ItemCallback<Client>() {
    override fun areItemsTheSame(oldItem: Client, newItem: Client): Boolean {
        return oldItem.clientId == newItem.clientId
    }

    override fun areContentsTheSame(oldItem: Client, newItem: Client): Boolean {
        return oldItem == newItem
    }
}