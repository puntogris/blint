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
import com.puntogris.blint.utils.getParentFab
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageSuppliersFragment : BaseFragmentOptions<FragmentManageSuppliersBinding>(R.layout.fragment_manage_suppliers) {

    private val viewModel: SupplierViewModel by viewModels()

    override fun initializeViews() {
        val manageProductsAdapter = ManageSuppliersAdapter{ onSupplierClickListener(it)}
        binding.recyclerView.adapter = manageProductsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launchWhenStarted {
            viewModel.getSuppliersPaging().collect {
                manageProductsAdapter.submitData(it)
            }
        }

        binding.supplierSearchText.addTextChangedListener {
            lifecycleScope.launch {
                viewModel.getSuppliersWithName(it.toString()).collect {
                    manageProductsAdapter.submitData(it)
                }
            }
        }
        getParentFab().setOnClickListener {
            findNavController().navigate(R.id.editSupplierFragment)
        }
    }

    private fun onSupplierClickListener(supplier: Supplier){
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