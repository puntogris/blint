package com.puntogris.blint.feature_store.presentation.orders.create_order

import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.databinding.FragmentReviewOrderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewOrderFragment :
    BaseFragment<FragmentReviewOrderBinding>(R.layout.fragment_review_order) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.createOrderGraphNav) {
        defaultViewModelProviderFactory
    }

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        registerToolbarBackButton(binding.reviewOrderToolbar)
    }

    fun navigateToPublishOrder() {
        findNavController().navigate(R.id.action_reviewRecordFragment_to_publishOrderFragment)
    }

    fun navigateToAddTrader() {
        findNavController().navigate(R.id.orderTraderBottomSheet)
    }

    fun navigateToAddDebt() {
        if (viewModel.isTraderValid()) {
            findNavController().navigate(R.id.orderDebtSelectorDialog)
        } else {
            UiInterface.showSnackBar(getString(R.string.snack_order_debt_requires_trader))
        }
    }
}
