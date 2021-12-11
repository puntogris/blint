package com.puntogris.blint.feature_store.presentation.product.traders

import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.databinding.FragmentProductSupplierBinding
import com.puntogris.blint.feature_store.domain.model.CheckableTrader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductTraderFragment :
    BaseFragment<FragmentProductSupplierBinding>(R.layout.fragment_product_supplier) {

    private val viewModel: ProductTraderViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        UiInterface.registerUi(showAppBar = false, showToolbar = false)

        setupCategoriesAdapter()
        registerToolbarBackButton(binding.searchToolbar)
    }

    private fun setupCategoriesAdapter() {
        ProductTraderAdapter { onSupplierClicked(it) }.let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ProductTraderAdapter) {
//        launchAndRepeatWithViewLifecycle {
//            viewModel.suppliersFlow.collect {
//                adapter.submitData(it)
//            }
//        }
    }

    private fun onSupplierClicked(trader: CheckableTrader) {
        //  viewModel.toggleSupplier(trader.supplier)
    }

    fun onDoneButtonClicked() {
//        setFragmentResult(
//            Keys.EDIT_PRODUCT_KEY,
//            bundleOf(Keys.PRODUCT_SUPPLIERS_KEY to viewModel.getFinalSuppliers())
//        )
//        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}