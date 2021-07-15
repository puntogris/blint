package com.puntogris.blint.ui.main

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.R
import com.puntogris.blint.diffcallback.MenuCardDiffCallback
import com.puntogris.blint.model.MenuCard

class MainMenuAdapter(private val clickListener: (MenuCard) -> Unit) : ListAdapter<MenuCard, MenuCardViewHolder>(MenuCardDiffCallback()) {

    init {
        val list = listOf(
            MenuCard(R.string.products_label, R.id.manageProductsFragment,R.drawable.ic_packages),
            MenuCard(R.string.suppliers_label, R.id.manageSuppliersFragment,R.drawable.ic_shipped),
            MenuCard(R.string.clients_label, R.id.manageClientsFragment,R.drawable.ic_rating),
            MenuCard(R.string.records_label, R.id.manageOrdersFragment, R.drawable.ic_report),
            MenuCard(R.string.reports_label, R.id.reportsFragment, R.drawable.ic_analytics),
            MenuCard(R.string.calendar_label, R.id.calendarFragment, R.drawable.ic_calendar),
            MenuCard(R.string.categories_label, R.id.manageCategoriesFragment, R.drawable.ic_category),
            MenuCard(R.string.debts_label, R.id.manageDebtFragment, R.drawable.ic_loan),
            MenuCard(R.string.account_label, R.id.accountPreferences, R.drawable.ic_profile)
            )
        submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuCardViewHolder {
        return MenuCardViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MenuCardViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}