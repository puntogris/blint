package com.puntogris.blint.ui.orders.detailed_order

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentReviewRecordBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ReviewRecordFragment: BaseFragment<FragmentReviewRecordBinding>(R.layout.fragment_review_record) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }

    private var debt = 0f

    override fun initializeViews() {
        setUpUi(showFab = true, showAppBar = false, showFabCenter = false, fabIcon = R.drawable.ic_baseline_arrow_forward_24){
            if (binding.debtAmountText.getString().toFloatOrNull() != null){
                viewModel.updateOrderDebt(binding.debtAmountText.getFloat())
            }else{
                showShortSnackBar(getString(R.string.snack_debt_value_error))
            }

            viewModel.updateOrderDebt(debt)

            findNavController().navigate(R.id.publishOrderFragment)

        }
        val items = resources.getStringArray(R.array.debt_type)
        binding.debtText.setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_item_list, items))

        binding.debtText.setOnItemClickListener { _, _, i, _ ->
            when(i){
                0 -> {
                    binding.debtAmount.gone()
                    binding.debtSummary.text = getString(R.string.order_debt_amount_summary, viewModel.order.value?.value, viewModel.order.value?.value)
                    viewModel.updateOrderDebt(0F)
                }
                1 -> {
                    if (viewModel.order.value?.traderId!!.isNotEmpty()){
                        binding.debtAmount.visible()
                        binding.debtSummary.text = getString(R.string.order_debt_amount_summary, viewModel.order.value?.value, viewModel.order.value?.value)
                    }else{
                        showSnackBarVisibilityAppBar(getString(R.string.snack_order_debt_trader_alert))
                        binding.debtText.setText(items[1])
                    }
                }
            }
        }

        binding.debtAmountText.addTextChangedListener{
            if (it.toString().isNotEmpty()){
                val debt = viewModel.order.value!!.value - it.toString().toFloat()
                binding.debtSummary.text = getString(R.string.order_debt_amount_summary, debt, viewModel.order.value?.value)
            }
        }

        binding.discountText.addTextChangedListener {
            val value = it.toString()
            if (value.isNotEmpty()){
                when {
                    binding.changeValueTypeText.getString() == "%" -> {
                        debt = (viewModel.order.value?.value!! * value.toFloat() / 100)
                        val newTotal = viewModel.order.value?.value!! - debt
                        binding.textView155.text = newTotal.toMoneyFormatted()
                    }
                    binding.changeValueTypeText.getString() == "$" -> {
                        val newTotal = viewModel.order.value?.value!! - value.toFloat()
                        binding.textView155.text = newTotal.toMoneyFormatted()
                        debt = value.toFloat()
                    }
                }
            }
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, listOf("%", "$"))
        (binding.changeValueTypeText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.changeValueTypeText.setOnItemClickListener { _, _, i, _ ->
            val value = binding.discountText.getString()

            if (value.isNotEmpty()){
                when(i){
                    0 -> {
                        debt = (viewModel.order.value?.value!! * value.toFloat() / 100)
                        val newTotal = viewModel.order.value?.value!! - debt
                        binding.textView155.text = newTotal.toMoneyFormatted()
                    }
                    1 -> {
                        val newTotal = viewModel.order.value?.value!! - value.toFloat()
                        binding.textView155.text = newTotal.toMoneyFormatted()
                        debt = value.toFloat()
                    }
                }
            }
        }

        viewModel.refreshOrderValue()
        binding.textView168.text = viewModel.order.value?.items?.size.toString()
        binding.textView155.text = viewModel.order.value?.items?.sumOf { it.value.toDouble() }.toString()
        binding.textView175.text = viewModel.getCurrentUserEmail()
        binding.textView171.text = Date().getDateWithTimeFormattedString()
        binding.textView178.text = if(viewModel.order.value?.traderName!!.isNotEmpty()) viewModel.order.value?.traderName else getString(R.string.not_specified)

    }

    override fun onDestroyView() {
        binding.debtText.setAdapter(null)
        super.onDestroyView()
    }
}