package com.puntogris.blint.ui.main

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.R
import com.puntogris.blint.diffcallback.MenuCardDiffCallback
import com.puntogris.blint.model.MenuCard
import com.puntogris.blint.utils.Constants.ACCOUNTING_CARD_CODE
import com.puntogris.blint.utils.Constants.ACCOUNT_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_CLIENTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_PRODUCTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_SUPPLIERS_CARD_CODE
import com.puntogris.blint.utils.Constants.CHARTS_CARD_CODE
import com.puntogris.blint.utils.Constants.DEB_CARD_CODE
import com.puntogris.blint.utils.Constants.TOOLS_CARD_CODE
import com.puntogris.blint.utils.Constants.RECORDS_CARD_CODE

class MainMenuAdapter(private val clickListener: (MenuCard) -> Unit) : ListAdapter<MenuCard, MenuCardViewHolder>(MenuCardDiffCallback()) {

    init {
        val list = listOf(
            MenuCard(ALL_PRODUCTS_CARD_CODE, "Productos", R.id.manageProductsFragment,R.drawable.ic_packages),
            MenuCard(ALL_SUPPLIERS_CARD_CODE, "Proveedores", R.id.manageSuppliersFragment,R.drawable.ic_shipped),
            MenuCard(ALL_CLIENTS_CARD_CODE, "Clientes", R.id.manageClientsFragment,R.drawable.ic_rating),
            MenuCard(RECORDS_CARD_CODE, "Ordenes", R.id.manageOrdersFragment, R.drawable.ic_report),
            MenuCard(CHARTS_CARD_CODE, "Informes", R.id.reportsFragment, R.drawable.ic_analytics),
            MenuCard(ACCOUNTING_CARD_CODE, "Agenda", R.id.calendarFragment, R.drawable.ic_calendar),
            MenuCard(TOOLS_CARD_CODE, "Herramientas", R.id.operationsFragment, R.drawable.ic_customer_support),
            MenuCard(DEB_CARD_CODE, "Deudas", R.id.manageDebtFragment, R.drawable.ic_loan),
            MenuCard(ACCOUNT_CARD_CODE,"Cuenta", R.id.accountPreferences, R.drawable.ic_profile)
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