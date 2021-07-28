package com.puntogris.blint.ui.product.suppliers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.RemoveProductSupplierVhBinding
import com.puntogris.blint.model.FirestoreSupplier

class RemoveProductSupplierViewHolder private constructor(val binding: RemoveProductSupplierVhBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(supplier: FirestoreSupplier, clickListener: (FirestoreSupplier) -> Unit){
        binding.supplier = supplier
        binding.categoryButton.setOnClickListener { clickListener(supplier) }
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): RemoveProductSupplierViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = RemoveProductSupplierVhBinding.inflate(layoutInflater,parent, false)
            return RemoveProductSupplierViewHolder(binding)
        }
    }
}
