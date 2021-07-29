package com.puntogris.blint.ui.product

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
import com.puntogris.blint.databinding.FragmentProductBinding
import com.puntogris.blint.model.Record
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.PRODUCT_DATA_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductFragment : BaseFragmentOptions<FragmentProductBinding>(R.layout.fragment_product) {

    private val args: ProductFragmentArgs by navArgs()
    private var mediator: TabLayoutMediator? = null
    private val viewModel: ProductViewModel by viewModels()

    override fun initializeViews() {
        registerUiInterface.register(showFab = true, fabIcon = R.drawable.ic_baseline_edit_24){
            navigateToEditProductFragment()
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
                            setOnClickListener { navigateToEditProductFragment() }
                        }
                    }
                    else -> {
                        getParentFab().apply {
                            changeIconFromDrawable(R.drawable.ic_baseline_add_24)
                            setOnClickListener { showOrderPickerAndNavigate(args.product?.product) }
                        }
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun navigateToEditProductFragment(){
        val action = ProductFragmentDirections.actionProductFragmentToEditProductFragment(args.product)
        findNavController().navigate(action)
    }

    private inner class ScreenSlidePagerAdapter(@NonNull parentFragment: FragmentManager) : FragmentStateAdapter(parentFragment, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            (if (position == 0 ) ProductDataFragment()
             else ProductRecordsFragment()).apply {
                arguments = Bundle().apply {
                    putParcelable(PRODUCT_DATA_KEY, args.product)
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editOption -> {
                navigateToEditProductFragment()
                true
            }
            R.id.deleteOption -> {
                InfoSheet().build(requireContext()) {
                    title(this@ProductFragment.getString(R.string.ask_delete_product_title))
                    content(this@ProductFragment.getString(R.string.delete_product_warning))
                    onNegative(this@ProductFragment.getString(R.string.action_cancel))
                    onPositive(this@ProductFragment.getString(R.string.action_yes)) { onDeleteProductConfirmed() }
                }.show(parentFragmentManager, "")
                true
            }
            R.id.createOrder -> {
                showOrderPickerAndNavigate(args.product?.product)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun onDeleteProductConfirmed(){
        lifecycleScope.launch {
            when (viewModel.deleteProductDatabase(args.product?.product!!.productId)) {
                SimpleResult.Failure ->
                    showLongSnackBarAboveFab(getString(R.string.snack_delete_product_error))
                SimpleResult.Success -> {
                    showLongSnackBarAboveFab(getString(R.string.snack_delete_product_success))
                    findNavController().navigateUp()
                }
            }
        }
    }

    fun navigateToInfoRecord(record: Record){
        val action = ProductFragmentDirections.actionProductFragmentToRecordInfoBottomSheet(record)
        findNavController().navigate(action)
    }

    fun navigateToSupplier(supplierId:String){
//        val action = ProductFragmentDirections.actionProductFragmentToSupplierFragment(s)
//        findNavController().navigate(action)
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.moreOptions).isVisible = true
        menu.findItem(R.id.createOrder).isVisible = true
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
    }
}