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
import com.google.android.material.tabs.TabLayoutMediator
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentClientBinding
import com.puntogris.blint.model.Record
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.CLIENT_DATA_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClientFragment : BaseFragmentOptions<FragmentClientBinding>(R.layout.fragment_client) {

    private val args: ClientFragmentArgs by navArgs()
    private var mediator: TabLayoutMediator? = null
    private val viewModel:ClientViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.register(showFab = true, fabIcon = R.drawable.ic_baseline_edit_24){
            navigateToEditClientFragment()
        }
        binding.viewPager.adapter = ScreenSlidePagerAdapter(childFragmentManager)
        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when(position){
                0 -> getString(R.string.tab_data)
                else -> getString(R.string.tab_records)
            }
        }
        mediator?.attach()

    }

    private fun navigateToEditClientFragment(){
        val action = ClientFragmentDirections.actionClientFragmentToEditClientFragment(args.client)
        findNavController().navigate(action)
    }

    private inner class ScreenSlidePagerAdapter(@NonNull parentFragment: FragmentManager) : FragmentStateAdapter(parentFragment, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            (if (position == 0 ) ClientDataFragment() else ClientRecordsFragment()).apply {
                arguments = Bundle().apply {
                    putParcelable(CLIENT_DATA_KEY, args.client)
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editOption -> {
                navigateToEditClientFragment()
                true
            }
            R.id.deleteOption -> {
                InfoSheet().build(requireContext()) {
                    title(this@ClientFragment.getString(R.string.ask_delete_client_title))
                    content(this@ClientFragment.getString(R.string.delete_client_warning))
                    onNegative(this@ClientFragment.getString(R.string.action_cancel))
                    onPositive(this@ClientFragment.getString(R.string.action_yes)) { onDeleteClientConfirmed() }
                }.show(parentFragmentManager, "")
                true
            }
            R.id.debtStatus -> {
                val action = ClientFragmentDirections.actionClientFragmentToDebtStatusFragment(client = args.client)
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
                    UiInterface.showSnackBar(getString(R.string.snack_delete_client_error))
                SimpleResult.Success -> {
                        UiInterface.showSnackBar(getString(R.string.snack_delete_client_success))
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