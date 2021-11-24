package com.puntogris.blint.ui.orders.detailed_order

import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentReviewRecordBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewRecordFragment :
    BaseFragment<FragmentReviewRecordBinding>(R.layout.fragment_review_record) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }

    override fun initializeViews() {
        binding.viewModel = viewModel

        UiInterface.registerUi(
            showFab = true,
            showAppBar = false,
            showFabCenter = false,
            fabIcon = R.drawable.ic_baseline_arrow_forward_24
        ) { navigateToPublishOrder() }

        setupOrderDebSelector()
    }

    private fun setupOrderDebSelector() {
        binding.orderDebtSelector.apply {
            setOnFocusChangeListener { _, _ -> hideKeyboard() }
            setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.dropdown_item_list,
                    resources.getStringArray(R.array.debt_type)
                )
            )
            setOnItemClickListener { _, _, i, _ ->
                binding.orderDebtAmountInputLayout.isVisible = i != 0
                if (i == 0) viewModel.updateOrderDebt(0F)
            }
        }
    }

    private fun navigateToPublishOrder() {
        if (viewModel.isDebtValid()) {
            findNavController().navigate(R.id.publishOrderFragment)
        } else {
            UiInterface.showSnackBar(getString(R.string.snack_debt_value_error))
        }
    }

    override fun onDestroyView() {
        binding.orderDebtSelector.setAdapter(null)
        super.onDestroyView()
    }
}