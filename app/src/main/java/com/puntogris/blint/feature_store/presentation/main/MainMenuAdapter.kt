package com.puntogris.blint.feature_store.presentation.main

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.R
import com.puntogris.blint.feature_store.domain.model.MenuCard

class MainMenuAdapter(private val clickListener: (MenuCard) -> Unit) :
    ListAdapter<MenuCard, RecyclerView.ViewHolder>(MenuCardDiffCallback()) {

    init {
        val list = listOf(
            MenuCard(R.string.administration, 0, 0),
            MenuCard(R.string.products_label, R.id.manageProductsFragment, R.drawable.ic_packages),
            MenuCard(R.string.suppliers_label, R.id.manageSuppliersFragment, R.drawable.ic_shipped),
            MenuCard(R.string.clients_label, R.id.manageClientsFragment, R.drawable.ic_rating),
            MenuCard(R.string.management, 0, 0),
            MenuCard(R.string.orders_label, R.id.manageOrdersFragment, R.drawable.ic_report),
            MenuCard(R.string.reports_label, R.id.reportsFragment, R.drawable.ic_analytics),
            MenuCard(R.string.debts_label, R.id.manageDebtFragment, R.drawable.ic_loan),
            MenuCard(R.string.shortcuts, 0, 0)
        )
        submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> HeaderViewHolder.from(parent)
            1 -> MenuCardViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //  if (isHeader(position)) {
        //       return
        //  }
        val item = getItem(position)
        return if (item.navigationId == 0) (holder as HeaderViewHolder).bind(getItem(position))
        else (holder as MenuCardViewHolder).bind(getItem(position), clickListener)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.navigationId == 0) 0 else 1
    }

    fun isHeader(position: Int) = position == 0 || position == 4

}