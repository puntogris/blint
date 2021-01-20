package com.puntogris.blint.ui.product

import android.os.Bundle
import android.view.Menu
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.maxkeppeler.bottomsheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentProductBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.changeIconFromDrawable
import com.puntogris.blint.utils.getParentFab
import com.puntogris.blint.utils.showLongSnackBarAboveFab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductFragment : BaseFragmentOptions<FragmentProductBinding>(R.layout.fragment_product) {

    private val args: ProductFragmentArgs by navArgs()
    private var mediator: TabLayoutMediator? = null
    private val viewModel: ProductViewModel by viewModels()

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
            (if (position == 0 ) ProductDataFragment() else ProductRecordsFragment()).apply {
                arguments = Bundle().apply {
                    putInt("product_key", args.productID)
                }
            }
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
    }

    override fun onEditButtonClicked() {
        val action = ProductFragmentDirections.actionProductFragmentToEditProductFragment(args.productID)
        findNavController().navigate(action)
    }

    override fun onDeleteButtonClicked() {
        InfoSheet().build(requireContext()) {
            title("Queres eliminar este producto?")
            content("Zona de peligro! Estas por eliminar un producto. Tene en cuenta que esta accion es irreversible.")
            onNegative("Cancelar")
            onPositive("Si") {
                    viewModel.deleteProductDatabase(args.productID)
                showLongSnackBarAboveFab("Producto eliminado correctamente.")
                findNavController().navigateUp()
            }
        }.show(parentFragmentManager, "")
    }

    override fun onCreateRecordButtonClicked() {
        val action = ProductFragmentDirections.actionProductFragmentToCreateRecordFragment(args.productID)
        findNavController().navigate(action)
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.moreOptions).isVisible = true
        menu.findItem(R.id.createRecord).isVisible = true
    }
}