package com.puntogris.blint.ui.custom_views

import android.os.Bundle

import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentMonthlyBinding
import com.puntogris.blint.ui.base.BaseFragment

class MonthlyFragment : BaseFragment<FragmentMonthlyBinding>(R.layout.fragment_monthly) {

    override fun initializeViews() {
        setUpRecyclerView()

    }

    private fun setUpRecyclerView() {
        binding.rvMonthly.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
           // addItemDecoration(getRallyItemDecoration())
            adapter = MonthlyAdapter(monthlyItems(arguments?.getInt(KEY_MONTH) ?: 1))
        }
    }

    fun monthlyItems(month: Int): List<MonthlyItem> {
        return listOf(
            MonthlyItem(month, "Genoe", 12.54f, ItemType.DECREASE),
            MonthlyItem(month, "ABC Payroll", 1141.12f, ItemType.INCREASE),
            MonthlyItem(month, "Farmer John's", 11.69f, ItemType.DECREASE),
            MonthlyItem(month, "Pilrose", 23.44f, ItemType.DECREASE),
            MonthlyItem(month, "Mainstom", 12.54f, ItemType.DECREASE),
            MonthlyItem(month, "Foodmates", 11.69f, ItemType.DECREASE),
            MonthlyItem(month, "Shrine", 23.44f, ItemType.DECREASE)
        ).shuffled()
    }

    companion object {
        private const val KEY_MONTH = "key-month"
        fun newInstance(month: Int): MonthlyFragment {
            return MonthlyFragment().apply {
                arguments = Bundle().apply { putInt(KEY_MONTH, month) }
            }
        }
    }
}
