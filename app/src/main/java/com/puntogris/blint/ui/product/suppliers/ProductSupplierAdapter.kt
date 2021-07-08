package com.puntogris.blint.ui.product.suppliers

import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.diffcallback.FirestoreSupplierDiffCallBack
import com.puntogris.blint.model.FirestoreSupplier
import com.puntogris.blint.ui.notifications.SwipeToDeleteCallback

class ProductSupplierAdapter: ListAdapter<FirestoreSupplier, ProductSupplierViewHolder>(
    FirestoreSupplierDiffCallBack()
) {

    private var suppliers = mutableListOf<FirestoreSupplier>()

    init {
        submitList(suppliers)
    }

    fun initialSuppliers(list:MutableList<FirestoreSupplier>){
        suppliers = list
    }

    fun getFinalSuppliers() = suppliers

    override fun getItemCount() = suppliers.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSupplierViewHolder {
        return ProductSupplierViewHolder.from(parent)
    }
    override fun onBindViewHolder(holder: ProductSupplierViewHolder, position: Int) {
        holder.bind(suppliers[position])
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewHolder.bindingAdapterPosition.apply {
                    suppliers.removeAt(this)
                    notifyItemRemoved(this)
                }
            }
        }.apply { ItemTouchHelper(this).attachToRecyclerView(recyclerView) }
    }

    fun addSupplier(supplier: FirestoreSupplier){
        if (!suppliers.contains(supplier)) suppliers.add(supplier)
        notifyDataSetChanged()

    }
}

