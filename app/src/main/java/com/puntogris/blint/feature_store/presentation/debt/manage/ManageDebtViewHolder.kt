package com.puntogris.blint.feature_store.presentation.debt.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.common.utils.capitalizeFirstChar
import com.puntogris.blint.common.utils.setDateFromTimestamp
import com.puntogris.blint.common.utils.setDebtColor
import com.puntogris.blint.databinding.DebtTraderVhBinding
import com.puntogris.blint.feature_store.domain.model.order.Debt

class ManageDebtViewHolder private constructor(val binding: DebtTraderVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(debt: Debt) {
        with(binding) {
            textViewDebtAmount.setDebtColor(debt.amount)
            textViewDebtDate.setDateFromTimestamp(debt.timestamp)
            textViewDebtTraderName.text = debt.traderName.capitalizeFirstChar()
        }
    }

    companion object {
        fun from(parent: ViewGroup): ManageDebtViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DebtTraderVhBinding.inflate(layoutInflater, parent, false)
            return ManageDebtViewHolder(binding)
        }
    }
}
