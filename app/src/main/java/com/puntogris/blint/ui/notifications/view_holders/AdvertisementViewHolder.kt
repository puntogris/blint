package com.puntogris.blint.ui.notifications.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.AdvertisementNotificationVhBinding
import com.puntogris.blint.model.notifications.AdvertisementNotification

class AdvertisementViewHolder private constructor(private val binding: AdvertisementNotificationVhBinding): RecyclerView.ViewHolder(
    binding.root
){
    fun bind(advertisementNotification: AdvertisementNotification, clickListener: (Any) -> Unit){
        binding.notification = advertisementNotification
        binding.root.setOnClickListener { clickListener(advertisementNotification) }
    }

    companion object{
        fun from(parent: ViewGroup): AdvertisementViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = AdvertisementNotificationVhBinding.inflate(layoutInflater, parent, false)
            return AdvertisementViewHolder(binding)
        }
    }
}
