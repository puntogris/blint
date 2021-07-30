package com.puntogris.blint.ui.orders.detailed_order

import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentReviewRecordBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewRecordFragment: BaseFragment<FragmentReviewRecordBinding>(R.layout.fragment_review_record) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }

    private var discount = 0F
    private var debt = 0F

    override fun initializeViews() {
        binding.viewModel = viewModel
        UiInterface.register(showFab = true, showAppBar = false, showFabCenter = false, fabIcon = R.drawable.ic_baseline_arrow_forward_24){
            val orderValue = viewModel.order.value?.value!!
            when {
                discount > orderValue -> UiInterface.showSnackBar(getString(R.string.snack_discount_limit))
                debt > orderValue -> UiInterface.showSnackBar(getString(R.string.snack_debt_value_error))
                else -> {
                    if (discount != 0F) viewModel.updateOrderDiscount(discount)
                    if (debt != 0F) viewModel.updateOrderDebt(debt)
                    findNavController().navigate(R.id.action_reviewRecordFragment_to_publishOrderFragment)
                }
            }
        }

        updateSummary()

        binding.debtText.apply {
            setOnFocusChangeListener { _, _ -> hideKeyboard() }
            val items = resources.getStringArray(R.array.debt_type)
            setAdapter(ArrayAdapter(requireContext(),R.layout.dropdown_item_list, items))
            setOnItemClickListener { _, _, i, _ ->
                when(i){
                    0 -> {
                        binding.debtAmount.gone()
                        binding.debtAmountText.setText("")
                        viewModel.updateOrderDebt(0F)
                    }
                    1 -> {
                        if (viewModel.order.value?.traderId!!.isNotEmpty()){
                            binding.debtAmount.visible()
                        }else{
                            UiInterface.showSnackBar(getString(R.string.snack_order_debt_trader_alert))
                            binding.debtText.setText(items[0])
                        }
                    }
                }
                updateSummary()
            }
        }

        binding.debtAmountText.addTextChangedListener{
            debt = if (it.toString().isBlank()) 0F else it.toString().toFloat()
            updateSummary()
        }

        binding.discountText.addTextChangedListener {
            val value = it.toString()
            val orderValue = viewModel.order.value?.value!!
            discount = if (value.isNotEmpty()){
                when {
                    binding.changeValueTypeText.getString() == "%" -> {
                         (orderValue * value.toFloat() / 100)
                    }
                    binding.changeValueTypeText.getString() == "$" -> {
                        value.toFloat()
                    }
                    else -> 0F
                }
            } else 0F
            binding.totalText.text = (orderValue - discount).toMoneyFormatted()
            updateSummary()
        }

        binding.changeValueTypeText.apply {
            setOnFocusChangeListener { _, _ -> hideKeyboard() }
            setAdapter(ArrayAdapter(requireContext(), R.layout.dropdown_item_list, listOf("%", "$")))
            setOnItemClickListener { _, _, i, _ ->
                val value = binding.discountText.getString()
                val orderValue = viewModel.order.value?.value!!

                discount = if (value.isNotEmpty()){
                    when(i){
                        0 -> (orderValue * value.toFloat() / 100)
                        1 -> value.toFloat()
                        else -> 0F
                    }
                }else 0F

                binding.totalText.text = (orderValue - discount).toMoneyFormatted()
                updateSummary()
            }
        }
    }

    private fun updateSummary(){
        val orderValue = viewModel.order.value?.value!!
        binding.debtSummary.text =
            getString(R.string.order_debt_amount_summary,
                (orderValue - discount - debt).toMoneyFormatted(),
                (orderValue - discount).toMoneyFormatted()
            )
    }

    override fun onDestroyView() {
        binding.changeValueTypeText.setAdapter(null)
        binding.debtText.setAdapter(null)
        super.onDestroyView()
    }
}