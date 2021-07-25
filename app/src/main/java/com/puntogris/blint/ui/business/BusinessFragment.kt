package com.puntogris.blint.ui.business

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentBusinessBinding
import com.puntogris.blint.model.Employee
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.ADMINISTRATOR
import com.puntogris.blint.utils.Constants.DISABLED
import com.puntogris.blint.utils.Constants.LOCAL
import com.puntogris.blint.utils.Constants.TO_DELETE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class BusinessFragment : BaseFragmentOptions<FragmentBusinessBinding>(R.layout.fragment_business) {

    private val args: BusinessFragmentArgs by navArgs()
    private val viewModel: BusinessViewModel by viewModels()
    private lateinit var businessEmployeeAdapter: BusinessEmployeeAdapter

    override fun initializeViews() {
        registerUiInterface.register()
        binding.employee = args.employee

        launchAndRepeatWithViewLifecycle {
            viewModel.getBusinessEmployees(args.employee.businessId).collect {
                when(it){
                    is UserBusiness.Error -> showLongSnackBarAboveFab(getString(R.string.snack_an_error_occurred))
                    UserBusiness.InProgress -> binding.progressBar.visible()
                    UserBusiness.NotFound -> onDataNotFound()
                    is UserBusiness.Success -> onDataFound(it.data)
                }
            }
        }

        if (args.employee.businessType == LOCAL){
            binding.textView103.gone()
            binding.cardView2.gone()
        }

        if(args.employee.businessStatus == TO_DELETE){
            binding.statusAlertStub.viewStub?.apply {
                layoutResource = R.layout.business_deleting_alert_view
                inflate()
            }
        }else if(args.employee.businessStatus == DISABLED){
            binding.statusAlertStub.viewStub?.apply {
                layoutResource = R.layout.business_deactivated_alert_view
                inflate()
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
        launchAndRepeatWithViewLifecycle {
            val userData = viewModel.hasUserOwnerPermissions(args.employee.businessId)
            if (userData.role == ADMINISTRATOR){
                menu.findItem(R.id.businessFragmentMenu).isVisible = true
                if(args.employee.businessType == LOCAL) {
                    menu.findItem(R.id.newEmployee).isVisible = false
                }
                if(args.employee.businessStatus == "ON_DELETE") {
                    menu.findItem(R.id.newEmployee).isVisible = false
                    menu.findItem(R.id.cancelDeletionBusiness).isVisible = true
                    menu.findItem(R.id.deleteBusiness).isVisible = false
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