package com.puntogris.blint.feature_store.presentation.product.traders

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentProductTraderBinding
import com.puntogris.blint.feature_store.domain.model.CheckableTrader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductTraderFragment : Fragment(R.layout.fragment_product_trader) {

    private val viewModel: ProductTraderViewModel by viewModels()

    private val binding by viewBinding(FragmentProductTraderBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbarBackButton(binding.toolbar)
        setupCategoriesAdapter()
        setupListeners()
    }

    private fun setupListeners() {
        binding.editTextTraderSearch.doAfterTextChanged { editable ->
            if (editable != null) {
                viewModel.setQuery(editable)
            }
        }
        binding.buttonDone.setOnClickListener {
            onDoneButtonClicked()
        }
    }

    private fun setupCategoriesAdapter() {
        val adapter = ProductTraderAdapter { onSupplierClicked(it) }
        binding.recyclerViewTraders.adapter = adapter
        subscribeUi(adapter)
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
        binding.recyclerViewTraders.adapter = null
        super.onDestroyView()
    }
}