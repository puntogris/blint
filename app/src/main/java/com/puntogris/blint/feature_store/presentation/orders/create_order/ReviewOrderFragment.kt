package com.puntogris.blint.feature_store.presentation.orders.create_order

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentReviewOrderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewOrderFragment : Fragment(R.layout.fragment_review_order) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.createOrderGraphNav) {
        defaultViewModelProviderFactory
    }

    private val binding by viewBinding(FragmentReviewOrderBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
