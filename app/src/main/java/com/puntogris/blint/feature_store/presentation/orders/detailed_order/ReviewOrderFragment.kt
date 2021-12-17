package com.puntogris.blint.feature_store.presentation.orders.detailed_order

import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.hideKeyboard
import com.puntogris.blint.databinding.FragmentReviewOrderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewOrderFragment :
    BaseFragment<FragmentReviewOrderBinding>(R.layout.fragment_review_order) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        UiInterface.registerUi(showAppBar = false)

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

    fun navigateToPublishOrder() {
        if (viewModel.isDebtValid()) {
            findNavController().navigate(R.id.action_reviewRecordFragment_to_publishOrderFragment)
        } else {
            UiInterface.showSnackBar(getString(R.string.snack_debt_value_error))
        }
    }

    override fun onDestroyView() {
        binding.orderDebtSelector.setAdapter(null)
        super.onDestroyView()
    }
}