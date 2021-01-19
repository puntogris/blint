package com.puntogris.blint.ui.custom_views

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.R
import com.puntogris.blint.utils.inflate
import com.puntogris.blint.utils.toMoneyFormatted

class MonthlyAdapter(private val items: List<MonthlyItem>) : RecyclerView.Adapter<MonthlyItemViewHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MonthlyItemViewHolder {
        return MonthlyItemViewHolder(parent.inflate(R.layout.item_monthly))
    }

    override fun onBindViewHolder(
        holder: MonthlyItemViewHolder,
        position: Int
    ) {
        holder.bind(items[position])
    }
}

class MonthlyItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    private val tvName: TextView = view.findViewById(R.id.tvName)
    private val tvAmount: TextView = view.findViewById(R.id.productAmountNetWorth)
    private val tvViewDollar: TextView = view.findViewById(R.id.text_view_dollar)
    private val tvDate: TextView = view.findViewById(R.id.tvDate)

    fun bind(model: MonthlyItem) {
        tvName.text = model.name
        tvAmount.text = model.amount.toMoneyFormatted()
        tvDate.text = model.date

        tvViewDollar.text = if (model.type == ItemType.INCREASE) {
            "+$"
        } else {
            "-$"
        }
    }
}