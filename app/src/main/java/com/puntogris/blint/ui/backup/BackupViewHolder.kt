package com.puntogris.blint.ui.backup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.BackupVhBinding
import com.puntogris.blint.model.Business
import com.puntogris.blint.model.Employee

class BackupViewHolder private constructor(val binding: BackupVhBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(business: Business) {
        binding.business = business
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): BackupViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = BackupVhBinding.inflate(layoutInflater,parent, false)
            return BackupViewHolder(binding)
        }
    }
}
