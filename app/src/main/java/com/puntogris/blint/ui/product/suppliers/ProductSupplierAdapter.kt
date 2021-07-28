package com.puntogris.blint.ui.product.suppliers

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.diffcallback.FirestoreSupplierDiffCallBack
import com.puntogris.blint.model.FirestoreSupplier
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.notifications.SwipeToDeleteCallback
import com.puntogris.blint.ui.supplier.manage.ManageSupplierViewHolder

class ProductSupplierAdapter(private val clickListener: (FirestoreSupplier) -> Unit): PagingDataAdapter<FirestoreSupplier, ProductSupplierViewHolder>(
    FirestoreSupplierDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSupplierViewHolder {
        return ProductSupplierViewHolder.from(parent)
    }
    override fun onBindViewHolder(holder: ProductSupplierViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

}

