package com.puntogris.blint.ui.product.suppliers

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentProductSupplierBinding
import com.puntogris.blint.model.FirestoreSupplier
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.UiInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductSupplierFragment :
    BaseFragment<FragmentProductSupplierBinding>(R.layout.fragment_product_supplier) {

    private val viewModel: ProductSupplierViewModel by viewModels()
    private val args: ProductSupplierFragmentArgs by navArgs()

    override fun initializeViews() {
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        UiInterface.registerUi(
            showFab = true,
            showAppBar = false,
            fabIcon = R.drawable.ic_baseline_arrow_forward_24,
            showToolbar = false,
            showFabCenter = false
        ) {
//            findNavController().apply {
//                previousBackStackEntry!!.savedStateHandle.set(
//                    PRODUCT_SUPPLIER_KEY,
//                    removeSupplierAdapter.getFinalSuppliers()
//                )
//                popBackStack()
//            }
        }

        setupCategoriesAdapter()

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

    private fun onRemoveSupplier(category: FirestoreSupplier) {

    }

    private fun onSupplierClicked(category: FirestoreSupplier) {

    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}