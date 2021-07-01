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
import com.google.android.material.tabs.TabLayoutMediator
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentSupplierBinding
import com.puntogris.blint.model.Record
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.ui.client.ClientFragmentDirections
import com.puntogris.blint.ui.product.ProductDataFragment
import com.puntogris.blint.ui.product.ProductFragmentDirections
import com.puntogris.blint.ui.product.ProductRecordsFragment
import com.puntogris.blint.utils.Constants
import com.puntogris.blint.utils.Constants.SUPPLIER_DEBT
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.setUpUi
import com.puntogris.blint.utils.showLongSnackBarAboveFab
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SupplierFragment : BaseFragmentOptions<FragmentSupplierBinding>(R.layout.fragment_supplier) {

    private val args: SupplierFragmentArgs by navArgs()
    private var mediator: TabLayoutMediator? = null
    private val viewModel: SupplierViewModel by viewModels()

    override fun initializeViews() {
        setUpUi(showFab = true)
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
                    putParcelable("supplier_key", args.supplier)
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editOption -> {
                val action = SupplierFragmentDirections.actionSupplierFragmentToEditSupplierFragment(supplier = args.supplier)
                findNavController().navigate(action)
                true
            }
            R.id.deleteOption -> {
                InfoSheet().build(requireContext()) {
                    title("Queres eliminar este proveedor?")
                    content("Zona de peligro! Estas por eliminar un proveedor. Tene en cuenta que esta accion es irreversible.")
                    onNegative("Cancelar")
                    onPositive("Si") { onDeleteSupplierConfirmed() }
                }.show(parentFragmentManager, "")
                true
            }
            R.id.debtStatus -> {
                val action = ClientFragmentDirections.actionClientFragmentToDebtStatusFragment(debtType = SUPPLIER_DEBT,id = args.supplier.supplierId)
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
                    showLongSnackBarAboveFab("Ocurrio un error al eliminar el proveedor.")
                SimpleResult.Success -> {
                    showLongSnackBarAboveFab("Proveedor eliminado correctamente.")
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