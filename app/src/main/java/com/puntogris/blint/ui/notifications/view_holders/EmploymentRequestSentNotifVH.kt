package com.puntogris.blint.ui.notifications.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.EmploymentRequestSentNotifVhBinding
import com.puntogris.blint.model.notifications.EmploymentRequestSentNotif

class EmploymentRequestSentNotifVH private constructor(private val binding: EmploymentRequestSentNotifVhBinding): RecyclerView.ViewHolder(
    binding.root
){
    fun bind(notification: EmploymentRequestSentNotif, clickListener: (EmploymentRequestSentNotif) -> Unit){
        binding.notification = notification
        binding.root.setOnClickListener { clickListener(notification) }

    }

    companion object{
        fun from(parent: ViewGroup): EmploymentRequestSentNotifVH {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = EmploymentRequestSentNotifVhBinding.inflate(layoutInflater, parent, false)
            return EmploymentRequestSentNotifVH(binding)
        }
    }
}
