package com.puntogris.blint.ui.supplier

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.ManageSuppliersVhBinding
import com.puntogris.blint.model.Supplier

class ManageSupplierViewHolder private constructor(val binding: ManageSuppliersVhBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(supplier: Supplier, clickListener: (Supplier)-> Unit) {
        binding.supplier = supplier
        binding.root.setOnClickListener { clickListener(supplier) }
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): ManageSupplierViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ManageSuppliersVhBinding.inflate(layoutInflater,parent, false)
            return ManageSupplierViewHolder(binding)
        }
    }
}
