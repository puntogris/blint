package com.puntogris.blint.ui.business.manage

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageBusinessBinding
import com.puntogris.blint.model.Business
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.gone
import com.puntogris.blint.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ManageBusinessFragment :
    BaseFragmentOptions<FragmentManageBusinessBinding>(R.layout.fragment_manage_business) {

    private val viewModel: ManageBusinessViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.registerUi()
        binding.fragment = this
        subscribeUi()
    }

    private fun subscribeUi() {
        launchAndRepeatWithViewLifecycle {
            viewModel.businesses.collect { data ->
                if (data.isNotEmpty()) {
                    ManageBusinessAdapter { onBusinessClicked(it) }.let {
                        binding.recyclerView.adapter = it
                        it.submitList(data)
                    }
                } else {
                    binding.businessEmptyUi.visible()
                }
                binding.progressBar.gone()
            }
        }
    }

    private fun onBusinessClicked(business: Business) {
        val action =
            ManageBusinessFragmentDirections.actionManageBusinessFragmentToBusinessFragment(business)
        findNavController().navigate(action)
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.manageBusinessMenu).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.newBusiness -> {
                findNavController().navigate(R.id.registerBusinessFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun onCreateNewBusinessClicked() {
        findNavController().navigate(R.id.registerBusinessFragment)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}