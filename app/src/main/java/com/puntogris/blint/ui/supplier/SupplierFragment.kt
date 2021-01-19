package com.puntogris.blint.ui.supplier

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.maxkeppeler.bottomsheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentSupplierBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.ui.product.ProductDataFragment
import com.puntogris.blint.ui.product.ProductFragmentDirections
import com.puntogris.blint.ui.product.ProductRecordsFragment
import com.puntogris.blint.utils.showLongSnackBarAboveFab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupplierFragment : BaseFragmentOptions<FragmentSupplierBinding>(R.layout.fragment_supplier, false) {

    private val args: SupplierFragmentArgs by navArgs()
    private var mediator: TabLayoutMediator? = null
    private val viewModel: SupplierViewModel by viewModels()

    override fun initializeViews() {
        val pagerAdapter = ScreenSlidePagerAdapter(childFragmentManager)
        binding.viewPager.adapter = pagerAdapter
        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position){
                0 -> "DATOS"
                else -> "MOVIMIENTOS"
            }
        }
        mediator?.attach()
    }

    private inner class ScreenSlidePagerAdapter(@NonNull parentFragment: FragmentManager) : FragmentStateAdapter(parentFragment, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            (if (position == 0 ) SupplierDataFragment() else SupplierRecordsFragment()).apply {
                arguments = Bundle().apply {
                    putInt("supplier_key", args.supplierID)
                }
            }
    }

    override fun onEditButtonClicked() {
        val action = SupplierFragmentDirections.actionSupplierFragmentToEditSupplierFragment(args.supplierID)
        findNavController().navigate(action)
    }

    override fun onDeleteButtonClicked() {
        InfoSheet().build(requireContext()) {
            title("Queres eliminar este proveedor?")
            content("Zona de peligro! Estas por eliminar un proveedor. Tene en cuenta que esta accion es irreversible.")
            onNegative("Cancelar")
            onPositive("Si") {
                viewModel.deleteSupplierDatabase(args.supplierID)
                showLongSnackBarAboveFab("Proveedor eliminado correctamente.")
                findNavController().navigateUp()
            }
        }.show(parentFragmentManager, "")
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
    }
}