package com.puntogris.blint.ui.supplier

import android.view.Menu
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentSupplierBinding
import com.puntogris.blint.model.order.Record
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.Constants.SUPPLIER_DATA_KEY
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.types.SimpleResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SupplierFragment : BaseFragmentOptions<FragmentSupplierBinding>(R.layout.fragment_supplier) {

    private val args: SupplierFragmentArgs by navArgs()
    private val viewModel: SupplierViewModel by viewModels()
    private var mediator: TabLayoutMediator? = null

    override fun initializeViews() {
        UiInterface.registerUi(showFab = true, fabIcon = R.drawable.ic_baseline_edit_24) {
            navigateToEditSupplierFragment()
        }
        binding.viewPager.adapter = ScreenSlidePagerAdapter(childFragmentManager)
        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_data)
                else -> getString(R.string.tab_records)
            }
        }
        mediator?.attach()

    }

    private fun navigateToEditSupplierFragment() {
        val action =
            SupplierFragmentDirections.actionSupplierFragmentToEditSupplierFragment(supplier = args.supplier)
        findNavController().navigate(action)
    }

    private inner class ScreenSlidePagerAdapter(@NonNull parentFragment: FragmentManager) :
        FragmentStateAdapter(parentFragment, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            (if (position == 0) SupplierDataFragment() else SupplierRecordsFragment())
                .apply {
                    arguments = bundleOf(SUPPLIER_DATA_KEY to args.supplier)
                }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editOption -> {
                navigateToEditSupplierFragment()
                true
            }
            R.id.deleteOption -> {
                InfoSheet().show(requireParentFragment().requireContext()) {
                    title(R.string.ask_delete_supplier_title)
                    content(R.string.delete_supplier_warning)
                    onNegative(R.string.action_cancel)
                    onPositive(R.string.action_yes) { onDeleteSupplierConfirmed() }
                }
                true
            }
            R.id.debtStatus -> {
                val action =
                    SupplierFragmentDirections.actionSupplierFragmentToDebtStatusFragment(supplier = args.supplier)
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onDeleteSupplierConfirmed() {
        lifecycleScope.launch {
            when (viewModel.deleteSupplierDatabase(args.supplier.supplierId)) {
                SimpleResult.Failure ->
                    UiInterface.showSnackBar(getString(R.string.snack_delete_supplier_error))
                SimpleResult.Success -> {
                    UiInterface.showSnackBar(getString(R.string.snack_delete_supplier_success))
                    findNavController().navigateUp()
                }
            }
        }
    }

    fun navigateToInfoRecord(record: Record) {
        val action =
            SupplierFragmentDirections.actionSupplierFragmentToRecordInfoBottomSheet(record)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.moreOptions).isVisible = true
        menu.findItem(R.id.debtStatus).isVisible = true
    }
}