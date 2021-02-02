package com.puntogris.blint.ui.main

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.MenuCardDiffCallback
import com.puntogris.blint.model.MenuCard
import com.puntogris.blint.utils.Constants
import com.puntogris.blint.utils.Constants.ACCOUNTING_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_CLIENTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_PRODUCTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_SUPPLIERS_CARD_CODE
import com.puntogris.blint.utils.Constants.CHARTS_CARD_CODE
import com.puntogris.blint.utils.Constants.OPERATIONS_CARD_CODE
import com.puntogris.blint.utils.Constants.RECORDS_CARD_CODE

class MainMenuAdapter(private val clickListener: (MenuCard) -> Unit) : ListAdapter<MenuCard, MenuCardViewHolder>(MenuCardDiffCallback()) {

    init {
        val list = listOf(
            MenuCard(ALL_PRODUCTS_CARD_CODE, "Productos"),
            MenuCard(ALL_CLIENTS_CARD_CODE, "Clientes"),
            MenuCard(ALL_SUPPLIERS_CARD_CODE, "Proveedores"),
            MenuCard(RECORDS_CARD_CODE, "Movimientos"),
            MenuCard(CHARTS_CARD_CODE, "Informes"),
            MenuCard(ACCOUNTING_CARD_CODE, "Agenda"),
            MenuCard(OPERATIONS_CARD_CODE, "Herramientas")
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