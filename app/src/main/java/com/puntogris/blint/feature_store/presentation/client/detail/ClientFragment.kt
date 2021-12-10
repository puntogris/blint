package com.puntogris.blint.feature_store.presentation.client.detail

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
import com.puntogris.blint.common.presentation.base.BaseFragmentOptions
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.databinding.FragmentClientBinding
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.domain.model.toTrader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ClientFragment : BaseFragmentOptions<FragmentClientBinding>(R.layout.fragment_client) {

    private val args: ClientFragmentArgs by navArgs()
    private var mediator: TabLayoutMediator? = null
    private val viewModel: ClientViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.registerUi(showFab = true, fabIcon = R.drawable.ic_baseline_edit_24) {
            navigateToEditClientFragment()
        }
        binding.viewPager.adapter = ScreenSlidePagerAdapter(childFragmentManager)
        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_data)
                else -> getString(R.string.tab_records)
            }
        }
        mediator?.attach()
    }

    private fun navigateToEditClientFragment() {
        val action = ClientFragmentDirections.actionClientFragmentToEditClientFragment(
            viewModel.currentClient.value
        )
        findNavController().navigate(action)
    }

    private inner class ScreenSlidePagerAdapter(@NonNull parentFragment: FragmentManager) :
        FragmentStateAdapter(parentFragment, viewLifecycleOwner.lifecycle) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment =
            (if (position == 0) ClientDataFragment() else ClientRecordsFragment())
                .apply {
                    arguments = args.toBundle()
                }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.editOption -> {
                navigateToEditClientFragment()
                true
            }
            R.id.deleteOption -> {
                InfoSheet().show(requireParentFragment().requireContext()) {
                    title(R.string.ask_delete_client_title)
                    content(R.string.delete_client_warning)
                    onNegative(R.string.action_cancel)
                    onPositive(R.string.action_yes) { onDeleteClientConfirmed() }
                }
                true
            }
            R.id.debtStatus -> {
                val action = ClientFragmentDirections.actionClientFragmentToDebtStatusFragment(
                    trader = viewModel.currentClient.value.toTrader()
                )
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onDeleteClientConfirmed() {
        lifecycleScope.launch {
            when (viewModel.deleteClient()) {
                is Resource.Error ->
                    UiInterface.showSnackBar(getString(R.string.snack_delete_client_error))
                is Resource.Success -> {
                    UiInterface.showSnackBar(getString(R.string.snack_delete_client_success))
                    findNavController().navigateUp()
                }
            }
        }
    }

    fun navigateToInfoRecord(record: Record) {
        val action = ClientFragmentDirections.actionGlobalRecordInfoBottomSheet(record)
        findNavController().navigate(action)
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.moreOptions).isVisible = true
        menu.findItem(R.id.debtStatus).isVisible = true
    }

    override fun onDestroyView() {
        mediator?.detach()
        mediator = null
        binding.viewPager.adapter = null
        super.onDestroyView()
    }
}