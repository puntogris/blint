package com.puntogris.blint.ui.product.suppliers

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentProductSupplierBinding
import com.puntogris.blint.model.FirestoreSupplier
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.supplier.manage.ManageSuppliersViewModel
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.CATEGORIES_SUPPLIERS_LIMIT
import com.puntogris.blint.utils.Constants.PRODUCT_SUPPLIER_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductSupplierFragment : BaseFragment<FragmentProductSupplierBinding>(R.layout.fragment_product_supplier) {

    private val viewModel: ProductSupplierViewModel by viewModels()
    private val args: ProductSupplierFragmentArgs by navArgs()
    private lateinit var searchAdapter: ProductSupplierAdapter
    private lateinit var removeSupplierAdapter: RemoveProductSupplierAdapter
    private var searchJob: Job? = null

    override fun initializeViews() {
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        registerUiInterface.register(showFab = true, showAppBar = false, fabIcon = R.drawable.ic_baseline_arrow_forward_24, showToolbar = false, showFabCenter = false){
            findNavController().apply {
                previousBackStackEntry!!.savedStateHandle.set(PRODUCT_SUPPLIER_KEY, removeSupplierAdapter.getFinalSuppliers())
                popBackStack()
            }
        }

        searchAdapter = ProductSupplierAdapter{ onAddSupplier(it)}
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }

        removeSupplierAdapter = RemoveProductSupplierAdapter { onRemoveSupplier(it) }
        binding.productCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = removeSupplierAdapter
        }

        if (args.suppliers.isNullOrEmpty()) binding.textView195.visible()
        else removeSupplierAdapter.initialSuppliers(args.suppliers!!.toMutableList())

        launchAndRepeatWithViewLifecycle {
            getAllSuppliersAndFillAdapter()
        }

        binding.categoriesSearch.addTextChangedListener {
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                it.toString().let {
                    if (it.isBlank()) getAllSuppliersAndFillAdapter()
                    else getAllSuppliersWithNameAndFillAdapter(it.lowercase())
                }
            }
        }
    }

    private suspend fun getAllSuppliersWithNameAndFillAdapter(text:String){
        viewModel.getSuppliersWithName(text).collect {
            searchAdapter.submitData(it)
        }
    }

    private suspend fun getAllSuppliersAndFillAdapter(){
        viewModel.getSuppliersPaging().collect {
            searchAdapter.submitData(it)
        }
    }

    private fun onAddSupplier(supplier: FirestoreSupplier){
        if (removeSupplierAdapter.itemCount >= CATEGORIES_SUPPLIERS_LIMIT)
            showSnackBarVisibilityAppBar(getString(R.string.snack_product_suppliers_limit, CATEGORIES_SUPPLIERS_LIMIT))
        else {
            removeSupplierAdapter.addSupplier(supplier)
            if (removeSupplierAdapter.itemCount != 0) binding.textView195.gone()
        }
    }

    private fun onRemoveSupplier(supplier: FirestoreSupplier){
        removeSupplierAdapter.removeSupplier(supplier)
        if (removeSupplierAdapter.itemCount == 0) binding.textView195.visible()
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        binding.productCategories.adapter = null
        super.onDestroyView()
    }
}