package com.puntogris.blint.ui.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.ManageClientsVhBinding
import com.puntogris.blint.model.Client

class ManageClientViewHolder private constructor(val binding: ManageClientsVhBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(client: Client, clickListener: (Client)-> Unit) {
        binding.client = client
        binding.root.setOnClickListener { clickListener(client) }
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): ManageClientViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ManageClientsVhBinding.inflate(layoutInflater,parent, false)
            return ManageClientViewHolder(binding)
        }
    }
}
