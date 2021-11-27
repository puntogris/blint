package com.puntogris.blint.feature_store.presentation.product.suppliers

import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.databinding.FragmentProductSupplierBinding
import com.puntogris.blint.feature_store.domain.model.CheckableSupplier
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductSupplierFragment :
    BaseFragment<FragmentProductSupplierBinding>(R.layout.fragment_product_supplier) {

    private val viewModel: ProductSupplierViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        UiInterface.registerUi(showAppBar = false, showToolbar = false)

        setupCategoriesAdapter()
        registerToolbarBackButton(binding.searchToolbar)

        //use xml for this todo
        binding.categoriesSearch.addTextChangedListener {
            viewModel.setQuery(it.toString())
        }
    }

    private fun setupCategoriesAdapter() {
        ProductSupplierAdapter { onSupplierClicked(it) }.let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ProductSupplierAdapter) {
        viewModel.suppliersLiveData.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun onSupplierClicked(supplier: CheckableSupplier) {
        viewModel.toggleSupplier(supplier.supplier)
    }

    fun onDoneButtonClicked() {
        setFragmentResult(
            Constants.EDIT_PRODUCT_KEY,
            bundleOf(Constants.PRODUCT_SUPPLIERS_KEY to viewModel.getFinalSuppliers())
        )
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}