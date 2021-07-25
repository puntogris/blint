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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductSupplierFragment : BaseFragment<FragmentProductSupplierBinding>(R.layout.fragment_product_supplier) {

    private val viewModel: ProductViewModel by viewModels()
    private val args: ProductSupplierFragmentArgs by navArgs()
    private lateinit var searchAdapter: ProductSupplierAdapter
    private var items = mutableListOf<FirestoreSupplier>()
    private var searchJob: Job? = null

    override fun initializeViews() {
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        registerUiInterface.register(showFab = true, showAppBar = false, fabIcon = R.drawable.ic_baseline_arrow_forward_24, showToolbar = false, showFabCenter = false){
            findNavController().apply {
                previousBackStackEntry!!.savedStateHandle.set("suppliers_key", searchAdapter.getFinalSuppliers())
                popBackStack()
            }
        }

        searchAdapter = ProductSupplierAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }

        args.suppliers?.let {
            searchAdapter.initialSuppliers(it.toMutableList())
        }

        binding.categoriesSearch.setOnItemClickListener { _, _, i, _ ->
            searchAdapter.addSupplier(items[i])
        }

        binding.categoriesSearch.addTextChangedListener {
            searchJob?.cancel()
            it?.toString()?.let { text ->
                searchJob = lifecycleScope.launch {
                    val data = viewModel.getSuppliersWithName(text)
                    items = data.toMutableList()
                    val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, data.map { it.companyName })
                    (binding.categoriesSearch as? AutoCompleteTextView)?.setAdapter(adapter)
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        binding.categoriesSearch.setAdapter(null)
        super.onDestroyView()
    }

}