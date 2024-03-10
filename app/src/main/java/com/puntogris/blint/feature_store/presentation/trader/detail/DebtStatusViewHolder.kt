package com.puntogris.blint.feature_store.presentation.trader.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.common.utils.setDateFromTimestampWithTime
import com.puntogris.blint.common.utils.setDebtColor
import com.puntogris.blint.databinding.DebtStatusVhBinding
import com.puntogris.blint.feature_store.domain.model.order.Debt

class DebtStatusViewHolder private constructor(val binding: DebtStatusVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(debt: Debt, clickListener: (Debt) -> (Unit)) {
        with(binding) {
            setDateFromTimestampWithTime(textViewDebtDate, debt.timestamp)
            setDebtColor(textViewDebtAmount, debt.amount)
            root.setOnClickListener { clickListener(debt) }
        }
    }

    companion object {
        fun from(parent: ViewGroup): DebtStatusViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DebtStatusVhBinding.inflate(layoutInflater, parent, false)
            return DebtStatusViewHolder(binding)
        }
    }
}
