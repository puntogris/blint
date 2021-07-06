package com.puntogris.blint.ui.supplier

import android.view.Menu
import android.view.MenuItem
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageSuppliersBinding
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageSuppliersFragment : BaseFragmentOptions<FragmentManageSuppliersBinding>(R.layout.fragment_manage_suppliers) {

    private val viewModel: SupplierViewModel by viewModels()
    private lateinit var manageProductsAdapter: ManageSuppliersAdapter

    override fun initializeViews() {
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        setUpUi(showToolbar = false, showAppBar = true, showFab = true)

        manageProductsAdapter = ManageSuppliersAdapter{ onSupplierClickListener(it)}
        binding.recyclerView.adapter = manageProductsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        launchAndRepeatWithViewLifecycle {
            getAllSuppliersAndFillAdapter()
        }

        binding.supplierSearch.addTextChangedListener {
            lifecycleScope.launch {
                it.toString().let {
                    if (it.isBlank()) getAllSuppliersAndFillAdapter()
                    else getAllSuppliersWithNameAndFillAdapter(it)
                }
            }
        }
        getParentFab().setOnClickListener {
            findNavController().navigate(R.id.editSupplierFragment)
        }
    }

    private suspend fun getAllSuppliersWithNameAndFillAdapter(text:String){
        viewModel.getSuppliersWithName(text).collect {
            manageProductsAdapter.submitData(it)
        }
    }

    private suspend fun getAllSuppliersAndFillAdapter(){
        viewModel.getSuppliersPaging().collect {
            manageProductsAdapter.submitData(it)
        }
    }

    private fun onSupplierClickListener(supplier: Supplier){
        hideKeyboard()
        val action = ManageSuppliersFragmentDirections.actionManageSuppliersFragmentToSupplierFragment(supplier)
        findNavController().navigate(action)
    }


    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.manageSuppliersFragmentMenu).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.newSupplier) {
            findNavController().navigate(R.id.editSupplierFragment)
            true
        }
        else super.onOptionsItemSelected(item)
    }
}