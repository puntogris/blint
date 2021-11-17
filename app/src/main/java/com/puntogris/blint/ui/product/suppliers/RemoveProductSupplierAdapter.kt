package com.puntogris.blint.ui.product.suppliers

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.FirestoreSupplierDiffCallBack
import com.puntogris.blint.model.FirestoreSupplier

class RemoveProductSupplierAdapter(private val clickListener: (FirestoreSupplier) -> Unit) :
    ListAdapter<FirestoreSupplier, RemoveProductSupplierViewHolder>(
        FirestoreSupplierDiffCallBack()
    ) {

    private var suppliers = mutableListOf<FirestoreSupplier>()

    init {
        submitList(suppliers)
    }

    fun initialSuppliers(list: MutableList<FirestoreSupplier>) {
        suppliers = list
    }

    fun getFinalSuppliers() = suppliers

    override fun getItemCount() = suppliers.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RemoveProductSupplierViewHolder {
        return RemoveProductSupplierViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RemoveProductSupplierViewHolder, position: Int) {
        holder.bind(suppliers[position], clickListener)
    }

    fun addSupplier(supplier: FirestoreSupplier) {
        if (!suppliers.contains(supplier)) suppliers.add(supplier)
        notifyDataSetChanged()
    }

    fun removeSupplier(supplier: FirestoreSupplier) {
        suppliers.remove(supplier)
        notifyDataSetChanged()
    }
}
