package com.puntogris.blint.ui.business.manage

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageBusinessBinding
import com.puntogris.blint.model.Employee
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ManageBusinessFragment : BaseFragmentOptions<FragmentManageBusinessBinding>(R.layout.fragment_manage_business) {

    private val viewModel: ManageBusinessViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.registerUi()
        binding.fragment = this
        launchAndRepeatWithViewLifecycle {
            val businesses = viewModel.getBusinessList()
            if (businesses.isNullOrEmpty()) onBusinessEmptyUi()
            else onBusinessFoundUi(businesses)
        }
    }

    private fun onBusinessFoundUi(employees: List<Employee>){
        binding.progressBar.gone()
        val businessAdapter = ManageBusinessAdapter{ onBusinessClicked(it) }
        binding.recyclerView.apply {
            adapter = businessAdapter
            layoutManager = LinearLayoutManager(requireContext())
            scheduleLayoutAnimation()
            businessAdapter.submitList(employees)
        }
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
            R.id.joinBusiness -> {
                findNavController().navigate(R.id.joinBusinessFragment)
                true
            }
            R.id.syncBusiness -> {
                findNavController().navigate(R.id.syncAccountFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onBusinessEmptyUi(){
        binding.apply {
            progressBar.gone()
            businessEmptyUi.visible()
        }
    }

    fun onCreateNewBusinessClicked(){
        findNavController().navigate(R.id.registerBusinessFragment)
    }

    fun onJoinBusinessClicked(){
        findNavController().navigate(R.id.joinBusinessFragment)
    }

    private fun onBusinessClicked(employee: Employee){
        val action = ManageBusinessFragmentDirections.actionManageBusinessFragmentToBusinessFragment(employee)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}