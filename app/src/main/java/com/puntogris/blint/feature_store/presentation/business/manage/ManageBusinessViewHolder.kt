package com.puntogris.blint.feature_store.presentation.business.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.BusinessItemVhBinding
import com.puntogris.blint.feature_store.domain.model.Business

class ManageBusinessViewHolder private constructor(val binding: BusinessItemVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        selectedBusiness: SelectedBusiness,
        clickListener: (Business) -> (Unit),
        selectListener: (Business) -> Unit
    ) {
        with(binding) {
            business = selectedBusiness
            root.setOnClickListener {
                clickListener(selectedBusiness.business)
            }
            selectBusinessButton.setOnClickListener {
                selectListener(selectedBusiness.business)
            }
            selectBusinessButton.isEnabled = !selectedBusiness.isSelected
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): ManageBusinessViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = BusinessItemVhBinding.inflate(layoutInflater, parent, false)
            return ManageBusinessViewHolder(binding)
        }
    }
}
