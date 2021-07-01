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
import com.google.android.material.tabs.TabLayoutMediator
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentProductBinding
import com.puntogris.blint.model.Record
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.setUpUi
import com.puntogris.blint.utils.showLongSnackBarAboveFab
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductFragment : BaseFragmentOptions<FragmentProductBinding>(R.layout.fragment_product) {

    private val args: ProductFragmentArgs by navArgs()
    private var mediator: TabLayoutMediator? = null
    private val viewModel: ProductViewModel by viewModels()

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
            (if (position == 0 ) ProductDataFragment() else ProductRecordsFragment()).apply {
                arguments = Bundle().apply {
                    putParcelable("product_key", args.product)
                }
            }
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editOption -> {
                val action = ProductFragmentDirections.actionProductFragmentToEditProductFragment(args.product)
                findNavController().navigate(action)
                true
            }
            R.id.deleteOption -> {
                InfoSheet().build(requireContext()) {
                    title("Queres eliminar este producto?")
                    content("Zona de peligro! Estas por eliminar un producto. Tene en cuenta que esta accion es irreversible.")
                    onNegative("Cancelar")
                    onPositive("Si") { onDeleteProductConfirmed() }
                }.show(parentFragmentManager, "")
                true
            }
            R.id.createRecord -> {
//                val action = ProductFragmentDirections.actionProductFragmentToCreateRecordFragment(args.productID)
//                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onDeleteProductConfirmed(){
        lifecycleScope.launch {
            when (viewModel.deleteProductDatabase(args.product?.product!!.productId)) {
                SimpleResult.Failure ->
                    showLongSnackBarAboveFab("Se produjo un error al eliminar el producto.")
                SimpleResult.Success -> {
                    showLongSnackBarAboveFab("Producto eliminado correctamente.")
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
//        val action = ProductFragmentDirections.actionProductFragmentToSupplierFragment(supplierId)
//        findNavController().navigate(action)
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.moreOptions).isVisible = true
        menu.findItem(R.id.createRecord).isVisible = true
    }
}