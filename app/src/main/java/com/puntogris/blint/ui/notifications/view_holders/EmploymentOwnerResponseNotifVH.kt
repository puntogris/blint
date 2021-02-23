package com.puntogris.blint.ui.notifications.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.EmploymentOwnerResponseNotifVhBinding
import com.puntogris.blint.model.notifications.EmploymentResponseOwnerNotif

class EmploymentOwnerResponseNotifVH private constructor(private val binding: EmploymentOwnerResponseNotifVhBinding): RecyclerView.ViewHolder(
    binding.root
){
    fun bind(notification: EmploymentResponseOwnerNotif, clickListener: (EmploymentResponseOwnerNotif) -> Unit){
        binding.notification = notification
        binding.root.setOnClickListener { clickListener(notification) }
    }
    companion object{
        fun from(parent: ViewGroup): EmploymentOwnerResponseNotifVH {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = EmploymentOwnerResponseNotifVhBinding.inflate(layoutInflater, parent, false)
            return EmploymentOwnerResponseNotifVH(binding)
        }
    }
}
