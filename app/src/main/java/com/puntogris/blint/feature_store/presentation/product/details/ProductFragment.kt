package com.puntogris.blint.feature_store.presentation.product.details

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
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.showOrderPickerAndNavigate
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.databinding.FragmentProductBinding
import com.puntogris.blint.feature_store.domain.model.order.Record
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductFragment : BaseFragment<FragmentProductBinding>(R.layout.fragment_product) {

    private val args: ProductFragmentArgs by navArgs()
    private var mediator: TabLayoutMediator? = null
    private val viewModel: ProductViewModel by viewModels()

    override fun initializeViews() {
        setupToolbar()
        setupPager()
    }

    private fun setupToolbar() {
        binding.productToolbar.apply {
            registerToolbarBackButton(this)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_edit_product -> navigateToEditProductFragment()
                    R.id.action_delete_product -> showDeleteProductDialog()
                    R.id.action_create_order_with_product -> {
                        showOrderPickerAndNavigate(args.productWithDetails.product)
                    }
                }
                true
            }
        }
    }

    private fun showDeleteProductDialog() {
        InfoSheet().show(requireParentFragment().requireContext()) {
            title(R.string.ask_delete_product_title)
            content(R.string.delete_product_warning)
            onNegative(R.string.action_cancel)
            onPositive(R.string.action_yes) { onDeleteProductConfirmed() }
        }
    }

    private fun onDeleteProductConfirmed() {
        lifecycleScope.launch {
            when (viewModel.deleteProductDatabase()) {
                is Resource.Error ->
                    UiInterface.showSnackBar(getString(R.string.snack_delete_product_error))
                is Resource.Success -> {
                    UiInterface.showSnackBar(getString(R.string.snack_delete_product_success))
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun setupPager() {
        binding.productViewPager.adapter = ScreenSlidePagerAdapter(childFragmentManager)

        mediator =
            TabLayoutMediator(binding.productTabLayout, binding.productViewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.tab_data)
                    else -> getString(R.string.tab_records)
                }
            }

        mediator?.attach()
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

    fun navigateToInfoRecord(record: Record) {
        val action = if (record.type == Constants.INITIAL) {
            ProductFragmentDirections.actionGlobalInitialRecordBottomSheet(record)
        } else {
            ProductFragmentDirections.actionGlobalOrderFragment(orderId = record.orderId)
        }
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.productViewPager.adapter = null
        super.onDestroyView()
    }
}