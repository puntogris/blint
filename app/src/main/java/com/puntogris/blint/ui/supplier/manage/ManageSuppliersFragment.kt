package com.puntogris.blint.ui.supplier.manage

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageSuppliersBinding
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.hideKeyboard
import com.puntogris.blint.utils.registerToolbarBackButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageSuppliersFragment :
    BaseFragmentOptions<FragmentManageSuppliersBinding>(R.layout.fragment_manage_suppliers) {

    private val viewModel: ManageSuppliersViewModel by viewModels()

    override fun initializeViews() {
        binding.viewModel = viewModel
        registerToolbarBackButton(binding.searchToolbar)

        UiInterface.registerUi(showToolbar = false, showAppBar = true, showFab = true) {
            findNavController().navigate(R.id.editSupplierFragment)
        }

        ManageSuppliersAdapter { onSupplierClickListener(it) }.let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ManageSuppliersAdapter) {
        viewModel.suppliersLiveData.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun onSupplierClickListener(supplier: Supplier) {
        hideKeyboard()
        val action =
            ManageSuppliersFragmentDirections.actionManageSuppliersFragmentToSupplierFragment(
                supplier
            )
        findNavController().navigate(action)
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.manageSuppliersFragmentMenu).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.newSupplier) {
            findNavController().navigate(R.id.editSupplierFragment)
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}