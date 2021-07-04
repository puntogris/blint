package com.puntogris.blint.ui.business

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentBusinessBinding
import com.puntogris.blint.model.Employee
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.UserBusiness
import com.puntogris.blint.utils.gone
import com.puntogris.blint.utils.showLongSnackBarAboveFab
import com.puntogris.blint.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class BusinessFragment : BaseFragmentOptions<FragmentBusinessBinding>(R.layout.fragment_business) {

    private val args: BusinessFragmentArgs by navArgs()
    private val viewModel: BusinessViewModel by viewModels()
    private lateinit var businessEmployeeAdapter: BusinessEmployeeAdapter

    override fun initializeViews() {
        binding.employee = args.employee

        lifecycleScope.launchWhenStarted {
            viewModel.getBusinessEmployees(args.employee.businessId).collect {
                when(it){
                    is UserBusiness.Error -> showLongSnackBarAboveFab(getString(R.string.snack_an_error_occurred))
                    UserBusiness.InProgress -> binding.progressBar.visible()
                    UserBusiness.NotFound -> onDataNotFound()
                    is UserBusiness.Success -> onDataFound(it.data)
                }
            }
        }
    }

    private fun onDataNotFound(){

    }

    private fun onDataFound(data:List<Employee>){
        binding.progressBar.gone()
        binding.recyclerView.apply {
            businessEmployeeAdapter = BusinessEmployeeAdapter{onEmployeeClicked(it)}
            adapter = businessEmployeeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            businessEmployeeAdapter.submitList(data)
        }
    }
    private fun onEmployeeClicked(employee:Employee){
        val action = BusinessFragmentDirections.actionBusinessFragmentToEmployeeFragment(employee)
        findNavController().navigate(action)
    }

    override fun setUpMenuOptions(menu: Menu) {
        lifecycleScope.launchWhenStarted {
            if (viewModel.hasUserOwnerPermissions(args.employee.employeeId)){
                menu.findItem(R.id.businessFragmentMenu).isVisible = true
                if(args.employee.businessType == "LOCAL") {
                    menu.findItem(R.id.newEmployee).isVisible = false
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteBusiness -> {
                val action = BusinessFragmentDirections.actionBusinessFragmentToDeleteBusinessFragment(args.employee)
                findNavController().navigate(action)
                true
            }
            R.id.newEmployee -> {
                val action = BusinessFragmentDirections.actionBusinessFragmentToAddBusinessEmployee(args.employee)
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}