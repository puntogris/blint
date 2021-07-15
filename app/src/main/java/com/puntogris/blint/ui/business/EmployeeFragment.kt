package com.puntogris.blint.ui.business

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentEmployeeBinding
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmployeeFragment : BaseFragmentOptions<FragmentEmployeeBinding>(R.layout.fragment_employee) {

    private val viewModel:BusinessViewModel by viewModels()
    private val args:EmployeeFragmentArgs by navArgs()

    override fun initializeViews() {
        binding.employee = args.employee
    }

    override fun setUpMenuOptions(menu: Menu) {
        launchAndRepeatWithViewLifecycle {
            if(viewModel.hasUserOwnerPermissions(args.employee.employeeId))
                menu.findItem(R.id.employeeFragmentMenu).isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteEmployee -> {
                true
            }
            R.id.changeEmployeeRole -> {true}
            else -> super.onOptionsItemSelected(item)
        }
    }
}