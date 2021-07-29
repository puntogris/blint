package com.puntogris.blint.ui.business

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentEmployeeBinding
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.Constants.ADMINISTRATOR
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.utils.UiInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmployeeFragment : BaseFragmentOptions<FragmentEmployeeBinding>(R.layout.fragment_employee) {

    private val viewModel:BusinessViewModel by viewModels()
    private val args:EmployeeFragmentArgs by navArgs()

    override fun initializeViews() {
        binding.employee = args.employee
    }

    override fun setUpMenuOptions(menu: Menu) {
        launchAndRepeatWithViewLifecycle {
            val userData = viewModel.hasUserOwnerPermissions(args.employee.businessId)
            if(userData.role == ADMINISTRATOR) {
                menu.findItem(R.id.employeeFragmentMenu).isVisible = true
            }
            menu.findItem(R.id.deleteEmployee).isVisible = userData.employeeId != args.employee.employeeId
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteEmployee -> {
                InfoSheet().build(requireContext()){
                    title(this@EmployeeFragment.getString(R.string.delete_employee_title))
                    content(this@EmployeeFragment.getString(R.string.delete_employee_warning))
                    onPositive(this@EmployeeFragment.getString(R.string.action_delete)) {
                        onConfirmDeleteEmployee()
                    }
                    onNegative(this@EmployeeFragment.getString(R.string.action_cancel))
                }.show(parentFragmentManager, "")
                true
            }
//            R.id.changeEmployeeRole -> {
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onConfirmDeleteEmployee(){
        lifecycleScope.launch {
            when(viewModel.deleteEmployeeFromBusiness(args.employee)){
                SimpleResult.Failure -> {
                    UiInterface.showSnackBar(getString(R.string.snack_delete_employee_error))
                }
                SimpleResult.Success -> {
                    UiInterface.showSnackBar(getString(R.string.snack_delete_employee_success))
                    findNavController().navigateUp()
                }
            }
        }
    }
}