package com.puntogris.blint.feature_store.presentation.store.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.BusinessItemVhBinding
import com.puntogris.blint.feature_store.domain.model.Store

class ManageStoreViewHolder private constructor(val binding: BusinessItemVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        selectedStore: SelectedStore,
        clickListener: (Store) -> (Unit),
        selectListener: (Store) -> Unit
    ) {
        with(binding) {
            business = selectedStore
            root.setOnClickListener {
                clickListener(selectedStore.store)
            }
            selectBusinessButton.setOnClickListener {
                selectListener(selectedStore.store)
            }
            selectBusinessButton.isEnabled = !selectedStore.isSelected
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): ManageStoreViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = BusinessItemVhBinding.inflate(layoutInflater, parent, false)
            return ManageStoreViewHolder(binding)
        }
    }
}
