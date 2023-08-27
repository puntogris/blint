package com.puntogris.blint.feature_store.presentation.product.traders

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.databinding.FragmentProductTraderBinding
import com.puntogris.blint.feature_store.domain.model.CheckableTrader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductTraderFragment :
    BaseFragment<FragmentProductTraderBinding>(R.layout.fragment_product_trader) {

    private val viewModel: ProductTraderViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this

        setupCategoriesAdapter()
        registerToolbarBackButton(binding.productTraderToolbar)
    }

    private fun setupCategoriesAdapter() {
        ProductTraderAdapter { onSupplierClicked(it) }.let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ProductTraderAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.tradersFlows.collect {
                adapter.submitData(it)
            }
        }
    }

    private fun onSupplierClicked(trader: CheckableTrader) {
        viewModel.toggleTrader(trader.trader)
    }

    fun onDoneButtonClicked() {
        setFragmentResult(
            Keys.EDIT_PRODUCT_KEY,
            bundleOf(Keys.PRODUCT_SUPPLIERS_KEY to viewModel.getFinalTraders())
        )
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}