package com.puntogris.blint.feature_store.presentation.main

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.R
import com.puntogris.blint.feature_store.domain.model.MenuCard

class MainMenuAdapter(private val clickListener: (MenuCard) -> Unit) :
    ListAdapter<MenuCard, MenuCardViewHolder>(MenuCardDiffCallback()) {

    init {
        val list = listOf(
            MenuCard(
                R.string.traders_label,
                R.id.manageTradersFragment,
                R.drawable.ic_traders_circle_blue
            ),
            MenuCard(
                R.string.calendar_label,
                R.id.calendarFragment,
                R.drawable.ic_calendar_circle_yellow
            ),
            MenuCard(
                R.string.reports_label,
                R.id.reportsNavGraph,
                R.drawable.ic_reports_circle_yellow
            ),
            MenuCard(
                R.string.categories_label,
                R.id.manageCategoriesFragment,
                R.drawable.ic_category_circle_blue
            ),
            MenuCard(
                R.string.debts_label,
                R.id.manageDebtFragment,
                R.drawable.ic_debts_circle_blue
            ),
            MenuCard(
                R.string.stores_label,
                R.id.manageStoreFragment,
                R.drawable.ic_store_circle_yellow
            ),
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