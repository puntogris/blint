package com.puntogris.blint.ui.main

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.MenuCardDiffCallback
import com.puntogris.blint.model.MenuCard

class MainMenuAdapter(private val clickListener: (MenuCard) -> Unit) : ListAdapter<MenuCard, MenuCardViewHolder>(MenuCardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuCardViewHolder {
        return MenuCardViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: MenuCardViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}