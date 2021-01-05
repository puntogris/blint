package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.Client

class ManageClientsDiffCallBack : DiffUtil.ItemCallback<Client>() {
    override fun areItemsTheSame(oldItem: Client, newItem: Client): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Client, newItem: Client): Boolean {
        return oldItem == newItem
    }
}