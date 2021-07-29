package com.puntogris.blint.ui.supplier

import android.os.Bundle
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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentSupplierBinding
import com.puntogris.blint.model.Record
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.ui.client.ClientFragmentDirections
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.SUPPLIER_DATA_KEY
import com.puntogris.blint.utils.Constants.SUPPLIER_DEBT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SupplierFragment : BaseFragmentOptions<FragmentSupplierBinding>(R.layout.fragment_supplier) {

    private val args: SupplierFragmentArgs by navArgs()
    private val viewModel: SupplierViewModel by viewModels()
    private var mediator: TabLayoutMediator? = null

    override fun initializeViews() {
        registerUiInterface.register(showFab = true, fabIcon = R.drawable.ic_baseline_edit_24){
            navigateToEditSupplierFragment()
        }
        binding.viewPager.adapter = ScreenSlidePagerAdapter(childFragmentManager)
        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position){
                0 -> getString(R.string.tab_data)
                else -> getString(R.string.tab_records)
            }
        }
        mediator?.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 -> {
                        getParentFab().apply {
                            changeIconFromDrawable(R.drawable.ic_baseline_edit_24)
                            setOnClickListener { navigateToEditSupplierFragment() }
                        }
                    }
                    else -> {
                        getParentFab().apply {
                            changeIconFromDrawable(R.drawable.ic_baseline_add_24)
                            setOnClickListener {

                            }
                        }
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun navigateToEditSupplierFragment(){
        val action = SupplierFragmentDirections.actionSupplierFragmentToEditSupplierFragment(supplier = args.supplier)
        findNavController().navigate(action)
    }

    private inner class ScreenSlidePagerAdapter(@NonNull parentFragment: FragmentManager) : FragmentStateAdapter(parentFragment, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            (if (position == 0 ) SupplierDataFragment() else SupplierRecordsFragment()).apply {
                arguments = Bundle().apply {
                    putParcelable(SUPPLIER_DATA_KEY, args.supplier)
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editOption -> {
                navigateToEditSupplierFragment()
                true
            }
            R.id.deleteOption -> {
                InfoSheet().build(requireContext()) {
                    title(this@SupplierFragment.getString(R.string.ask_delete_supplier_title))
                    content(this@SupplierFragment.getString(R.string.delete_supplier_warning))
                    onNegative(this@SupplierFragment.getString(R.string.action_cancel))
                    onPositive(this@SupplierFragment.getString(R.string.action_yes)) { onDeleteSupplierConfirmed() }
                }.show(parentFragmentManager, "")
                true
            }
            R.id.debtStatus -> {
                val action = SupplierFragmentDirections.actionSupplierFragmentToDebtStatusFragment(supplier = args.supplier)
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onDeleteSupplierConfirmed(){
        lifecycleScope.launch {
            when(viewModel.deleteSupplierDatabase(args.supplier.supplierId)){
                SimpleResult.Failure ->
                    showLongSnackBarAboveFab(getString(R.string.snack_delete_supplier_error))
                SimpleResult.Success -> {
                    showLongSnackBarAboveFab(getString(R.string.snack_delete_supplier_success))
                    findNavController().navigateUp()
                }
            }
        }
    }

    fun navigateToInfoRecord(record: Record){
        val action = SupplierFragmentDirections.actionSupplierFragmentToRecordInfoBottomSheet(record)
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