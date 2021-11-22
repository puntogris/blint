package com.puntogris.blint.ui.product.details

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
import com.puntogris.blint.databinding.FragmentProductBinding
import com.puntogris.blint.model.order.Record
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.addOnTabSelectedListener
import com.puntogris.blint.utils.showOrderPickerAndNavigate
import com.puntogris.blint.utils.types.SimpleResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductFragment : BaseFragmentOptions<FragmentProductBinding>(R.layout.fragment_product) {

    private val args: ProductFragmentArgs by navArgs()
    private var mediator: TabLayoutMediator? = null
    private val viewModel: ProductViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.registerUi(showFab = true, fabIcon = R.drawable.ic_baseline_edit_24) {
            navigateToEditProductFragment()
        }

        binding.viewPager.adapter = ScreenSlidePagerAdapter(childFragmentManager)

        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_data)
                else -> getString(R.string.tab_records)
            }
        }

        mediator?.attach()

        binding.tabLayout.addOnTabSelectedListener {
            when (it) {
                0 -> UiInterface.setFabImageAndClickListener(R.drawable.ic_baseline_edit_24) {
                    navigateToEditProductFragment()
                }
                else -> UiInterface.setFabImageAndClickListener {
                    showOrderPickerAndNavigate(args.productWithDetails.product)
                }
            }
        }
    }

    private fun navigateToEditProductFragment() {
        val action =
            ProductFragmentDirections.actionProductFragmentToEditProductFragment(args.productWithDetails)
        findNavController().navigate(action)
    }

    private inner class ScreenSlidePagerAdapter(@NonNull parentFragment: FragmentManager) :
        FragmentStateAdapter(parentFragment, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            (if (position == 0) ProductDataFragment() else ProductRecordsFragment())
                .apply {
                    arguments = args.toBundle()
                }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editOption -> {
                navigateToEditProductFragment()
                true
            }
            R.id.deleteOption -> {
                InfoSheet().show(requireParentFragment().requireContext()) {
                    title(R.string.ask_delete_product_title)
                    content(R.string.delete_product_warning)
                    onNegative(R.string.action_cancel)
                    onPositive(R.string.action_yes) { onDeleteProductConfirmed() }
                }
                true
            }
            R.id.createOrder -> {
                showOrderPickerAndNavigate(args.productWithDetails.product)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onDeleteProductConfirmed() {
        lifecycleScope.launch {
            when (viewModel.deleteProductDatabase(args.productWithDetails.product.productId)) {
                SimpleResult.Failure ->
                    UiInterface.showSnackBar(getString(R.string.snack_delete_product_error))
                SimpleResult.Success -> {
                    UiInterface.showSnackBar(getString(R.string.snack_delete_product_success))
                    findNavController().navigateUp()
                }
            }
        }
    }

    fun navigateToInfoRecord(record: Record) {
        val action = ProductFragmentDirections.actionProductFragmentToRecordInfoBottomSheet(record)
        findNavController().navigate(action)
    }

    fun navigateToSupplier(supplierId: String) {
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