package com.puntogris.blint.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.NotificationVhBinding
import com.puntogris.blint.model.Notification
import com.puntogris.blint.utils.gone

class NotificationViewHolder private constructor(val binding: NotificationVhBinding): RecyclerView.ViewHolder(
    binding.root
){
    fun bind(notification: Notification, clickListener: (Notification) -> Unit){
        binding.apply {
            notification.let {
                this.notification = it
                if (it.imageUri.isBlank()) image.gone()
                if (it.navigationUri.isNotBlank()) root.setOnClickListener { clickListener(notification) }
            }
            executePendingBindings()
        }

    }

    companion object{
        fun from(parent: ViewGroup): NotificationViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = NotificationVhBinding.inflate(layoutInflater, parent, false)
            return NotificationViewHolder(binding)
        }
    }
}
