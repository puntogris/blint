package com.puntogris.blint.feature_store.presentation.supplier.detail

import android.view.Menu
import android.view.MenuItem
import androidx.annotation.NonNull
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
import com.puntogris.blint.common.presentation.base.BaseFragmentOptions
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.databinding.FragmentSupplierBinding
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.domain.model.toTrader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
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
                    arguments = args.toBundle()
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
                    SupplierFragmentDirections.actionSupplierFragmentToDebtStatusFragment(
                        trader = viewModel.currentSupplier.value.toTrader()
                    )
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onDeleteSupplierConfirmed() {
        lifecycleScope.launch {
            when (viewModel.deleteSupplier()) {
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
            SupplierFragmentDirections.actionGlobalRecordInfoBottomSheet(record)
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