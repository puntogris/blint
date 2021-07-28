package com.puntogris.blint.ui.product.suppliers

import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.diffcallback.FirestoreSupplierDiffCallBack
import com.puntogris.blint.model.FirestoreSupplier
import com.puntogris.blint.ui.notifications.SwipeToDeleteCallback

class ProductSupplierAdapter(private val clickListener: (FirestoreSupplier) -> Unit): ListAdapter<FirestoreSupplier, ProductSupplierViewHolder>(
    FirestoreSupplierDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSupplierViewHolder {
        return ProductSupplierViewHolder.from(parent)
    }
    override fun onBindViewHolder(holder: ProductSupplierViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

}

