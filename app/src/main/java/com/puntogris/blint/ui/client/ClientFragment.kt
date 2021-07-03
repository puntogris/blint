package com.puntogris.blint.ui.client

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
import com.puntogris.blint.databinding.FragmentClientBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Record
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.ui.product.ProductDataFragment
import com.puntogris.blint.ui.product.ProductFragmentDirections
import com.puntogris.blint.ui.product.ProductRecordsFragment
import com.puntogris.blint.ui.supplier.SupplierFragmentDirections
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.CLIENT_DEBT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClientFragment : BaseFragmentOptions<FragmentClientBinding>(R.layout.fragment_client) {

    private val args: ClientFragmentArgs by navArgs()
    private var mediator: TabLayoutMediator? = null
    private val viewModel:ClientViewModel by viewModels()

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

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 -> {
                        getParentFab().apply {
                            changeIconFromDrawable(R.drawable.ic_baseline_edit_24)
                            setOnClickListener {
                                val action = ClientFragmentDirections.actionClientFragmentToEditClientFragment(args.client)
                                findNavController().navigate(action)
                            }
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

    private inner class ScreenSlidePagerAdapter(@NonNull parentFragment: FragmentManager) : FragmentStateAdapter(parentFragment, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            (if (position == 0 ) ClientDataFragment() else ClientRecordsFragment()).apply {
                arguments = Bundle().apply {
                    putParcelable("client_key", args.client)
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editOption -> {
                val action = ClientFragmentDirections.actionClientFragmentToEditClientFragment(args.client)
                findNavController().navigate(action)
                true
            }
            R.id.deleteOption -> {
                InfoSheet().build(requireContext()) {
                    title("Queres eliminar este cliente?")
                    content("Zona de peligro! Estas por eliminar un cliente. Tene en cuenta que esta accion es irreversible.")
                    onNegative("Cancelar")
                    onPositive("Si") { onDeleteClientConfirmed() }
                }.show(parentFragmentManager, "")
                true
            }
            R.id.debtStatus -> {
                val action = ClientFragmentDirections.actionClientFragmentToDebtStatusFragment(debtType = CLIENT_DEBT, id = args.client.clientId)
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onDeleteClientConfirmed(){
        lifecycleScope.launch {
            when(viewModel.deleteClientDatabase(args.client.clientId)){
                SimpleResult.Failure ->
                    showLongSnackBarAboveFab("Ocurrio un error al eliminar al cliente.")
                SimpleResult.Success -> {
                    showLongSnackBarAboveFab("Cliente eliminado correctamente.")
                    findNavController().navigateUp()
                }
            }
        }
    }

    fun navigateToInfoRecord(record: Record){
        val action = ClientFragmentDirections.actionClientFragmentToRecordInfoBottomSheet(record)
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