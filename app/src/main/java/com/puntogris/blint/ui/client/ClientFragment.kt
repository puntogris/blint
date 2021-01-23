package com.puntogris.blint.ui.client

import android.os.Bundle
import android.view.Menu
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentClientBinding
import com.puntogris.blint.model.Record
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.ui.product.ProductDataFragment
import com.puntogris.blint.ui.product.ProductFragmentDirections
import com.puntogris.blint.ui.product.ProductRecordsFragment
import com.puntogris.blint.ui.supplier.SupplierFragmentDirections
import com.puntogris.blint.utils.showLongSnackBarAboveFab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientFragment : BaseFragmentOptions<FragmentClientBinding>(R.layout.fragment_client) {

    private val args: ClientFragmentArgs by navArgs()
    private var mediator: TabLayoutMediator? = null
    private val viewModel:ClientViewModel by viewModels()

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
            (if (position == 0 ) ClientDataFragment() else ClientRecordsFragment()).apply {
                arguments = Bundle().apply {
                    putInt("client_key", args.clientID)
                }
            }
    }

    override fun onEditButtonClicked() {
        val action = ClientFragmentDirections.actionClientFragmentToEditClientFragment(args.clientID)
        findNavController().navigate(action)
    }

    fun navigateToInfoRecord(record: Record){
        val action = ClientFragmentDirections.actionClientFragmentToRecordInfoBottomSheet(record)
        findNavController().navigate(action)
    }

    override fun onDeleteButtonClicked() {
        InfoSheet().build(requireContext()) {
            title("Queres eliminar este cliente?")
            content("Zona de peligro! Estas por eliminar un cliente. Tene en cuenta que esta accion es irreversible.")
            onNegative("Cancelar")
            onPositive("Si") {
                viewModel.deleteClientDatabase(args.clientID)
                showLongSnackBarAboveFab("Cliente eliminado correctamente.")
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

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.moreOptions).isVisible = true
    }
}