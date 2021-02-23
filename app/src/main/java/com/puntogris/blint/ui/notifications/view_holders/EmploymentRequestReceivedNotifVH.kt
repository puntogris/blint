package com.puntogris.blint.ui.notifications.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.EmploymentRequestReceivedNotifVhBinding
import com.puntogris.blint.model.notifications.EmploymentRequestReceivedNotif

class EmploymentRequestReceivedNotifVH private constructor(private val binding: EmploymentRequestReceivedNotifVhBinding): RecyclerView.ViewHolder(
    binding.root
){
    fun bind(notification: EmploymentRequestReceivedNotif, clickListener: (EmploymentRequestReceivedNotif) -> Unit){
        binding.notification = notification
        binding.root.setOnClickListener { clickListener(notification) }
    }

    companion object{
        fun from(parent: ViewGroup): EmploymentRequestReceivedNotifVH {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = EmploymentRequestReceivedNotifVhBinding.inflate(layoutInflater, parent, false)
            return EmploymentRequestReceivedNotifVH(binding)
        }
    }
}