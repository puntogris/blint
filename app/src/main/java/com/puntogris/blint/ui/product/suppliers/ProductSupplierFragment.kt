package com.puntogris.blint.ui.product.suppliers

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import com.puntogris.blint.ui.product.ProductViewModel
import com.puntogris.blint.utils.registerUiInterface
import com.puntogris.blint.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductSupplierFragment : BaseFragment<FragmentProductSupplierBinding>(R.layout.fragment_product_supplier) {

    private val viewModel: ProductViewModel by viewModels()
    private val args: ProductSupplierFragmentArgs by navArgs()
    private lateinit var searchAdapter: ProductSupplierAdapter
    private lateinit var removeSupplierAdapter: RemoveProductSupplierAdapter
    private var items = mutableListOf<FirestoreSupplier>()
    private var searchJob: Job? = null

    override fun initializeViews() {
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        registerUiInterface.register(showFab = true, showAppBar = false, fabIcon = R.drawable.ic_baseline_arrow_forward_24, showToolbar = false, showFabCenter = false){
            findNavController().apply {
                previousBackStackEntry!!.savedStateHandle.set("suppliers_key", removeSupplierAdapter.getFinalSuppliers())
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

        binding.categoriesSearch.addTextChangedListener {
            searchJob?.cancel()
            val text = it.toString()
            searchJob = lifecycleScope.launch {
                if (text.isBlank()){

                }else{
                    val data = viewModel.getSuppliersWithName(text)
                    items = data.toMutableList()
                }
            }
        }
    }

    private fun onAddSupplier(supplier: FirestoreSupplier){
        removeSupplierAdapter.addSupplier(supplier)
    }

    private fun onRemoveSupplier(supplier: FirestoreSupplier){
        removeSupplierAdapter.removeSupplier(supplier)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        binding.productCategories.adapter = null
        super.onDestroyView()
    }
}