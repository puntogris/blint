package com.puntogris.blint.ui.business

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentBusinessBinding
import com.puntogris.blint.model.Business
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.DISABLED
import com.puntogris.blint.utils.Constants.TO_DELETE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessFragment : BaseFragmentOptions<FragmentBusinessBinding>(R.layout.fragment_business) {

    private val args: BusinessFragmentArgs by navArgs()
    private val viewModel: BusinessViewModel by viewModels()
    private lateinit var businessEmployeeAdapter: BusinessEmployeeAdapter

    override fun initializeViews() {
        UiInterface.registerUi()
        binding.employee = args.business

        launchAndRepeatWithViewLifecycle {
//            viewModel.getBusinessEmployees(args.business.businessId).collect {
//                when(it){
//                    is UserBusiness.Error -> UiInterface.showSnackBar(getString(R.string.snack_error_connection_server_try_later))
//                    UserBusiness.InProgress -> binding.progressBar.visible()
//                    UserBusiness.NotFound -> onDataNotFound()
//                    is UserBusiness.Success -> onDataFound(it.data)
//                }
//            }
        }

            binding.textView103.gone()
            binding.cardView2.gone()


        if(args.business.status == TO_DELETE){
            binding.statusAlertStub.viewStub?.apply {
                layoutResource = R.layout.business_deleting_alert_view
                inflate()
            }
        }else if(args.business.status == DISABLED){
            binding.statusAlertStub.viewStub?.apply {
                layoutResource = R.layout.business_deactivated_alert_view
                inflate()
            }
        }
    }

    private fun onDataNotFound(){
        UiInterface.showSnackBar(getString(R.string.snack_error_connection_server_try_later))
    }

    private fun onDataFound(data:List<Business>){
        binding.progressBar.gone()
        binding.recyclerView.apply {
            businessEmployeeAdapter = BusinessEmployeeAdapter{onEmployeeClicked(it)}
            adapter = businessEmployeeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            businessEmployeeAdapter.submitList(data)
        }
    }
    private fun onEmployeeClicked(business:Business){
//        val action = BusinessFragmentDirections.actionBusinessFragmentToEmployeeFragment(business)
//        findNavController().navigate(action)
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.businessFragmentMenu).isVisible = true
        if(args.business.status == TO_DELETE) {
            menu.findItem(R.id.newEmployee).isVisible = false
            menu.findItem(R.id.cancelDeletionBusiness).isVisible = true
            menu.findItem(R.id.deleteBusiness).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteBusiness -> {
                val action = BusinessFragmentDirections.actionBusinessFragmentToDeleteBusinessFragment(args.business)
                findNavController().navigate(action)
                true
            }
            R.id.newEmployee -> {
//                val action = BusinessFragmentDirections.actionBusinessFragmentToAddBusinessEmployee(args.employee)
//                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}