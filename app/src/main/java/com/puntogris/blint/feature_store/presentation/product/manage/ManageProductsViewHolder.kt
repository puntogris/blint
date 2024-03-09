package com.puntogris.blint.feature_store.presentation.product.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.common.utils.capitalizeFirstChar
import com.puntogris.blint.common.utils.setProductPrices
import com.puntogris.blint.databinding.ManageProductsVhBinding
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails

class ManageProductsViewHolder private constructor(val binding: ManageProductsVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        product: ProductWithDetails,
        shortClickListener: (ProductWithDetails) -> Unit,
        longClickListener: (ProductWithDetails) -> Unit
    ) {
        with(binding) {
            textViewProductName.text = product.product.name.capitalizeFirstChar()
            textViewProductPrices.setProductPrices(product.product)
            textViewProductStock.text = product.product.stock.toString()
            root.setOnClickListener { shortClickListener(product) }
            root.setOnLongClickListener {
                longClickListener(product)
                true
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): ManageProductsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ManageProductsVhBinding.inflate(layoutInflater, parent, false)
            return ManageProductsViewHolder(binding)
        }
    }
}
