package com.puntogris.blint.ui.notifications.view_holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.EmploymentEmployeeResponseNotifVhBinding
import com.puntogris.blint.model.notifications.EmploymentResponseEmployeeNotif

class EmploymentEmployeeResponseNotifVH private constructor(private val binding: EmploymentEmployeeResponseNotifVhBinding): RecyclerView.ViewHolder(
    binding.root
){
    fun bind(notification: EmploymentResponseEmployeeNotif, clickListener: (EmploymentResponseEmployeeNotif) -> Unit){
        binding.notification = notification
        binding.root.setOnClickListener { clickListener(notification) }
    }

    companion object{
        fun from(parent: ViewGroup): EmploymentEmployeeResponseNotifVH {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = EmploymentEmployeeResponseNotifVhBinding.inflate(layoutInflater, parent, false)
            return EmploymentEmployeeResponseNotifVH(binding)
        }
    }
}