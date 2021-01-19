package com.puntogris.blint.ui.record

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.ProductRecordsVhBinding
import com.puntogris.blint.model.Record

class ManageRecordsViewHolder private constructor(val binding: ProductRecordsVhBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(record: Record, clickListener: (Record)-> Unit) {
        binding.record = record
        binding.root.setOnClickListener { clickListener(record) }
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): ManageRecordsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ProductRecordsVhBinding.inflate(layoutInflater,parent, false)
            return ManageRecordsViewHolder(binding)
        }
    }
}
