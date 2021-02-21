package com.puntogris.blint.ui.business

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentAddBusinessEmployeeBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class AddBusinessEmployee : BaseFragment<FragmentAddBusinessEmployeeBinding>(R.layout.fragment_add_business_employee) {

    private val viewModel: BusinessViewModel by viewModels()

    override fun initializeViews() {
        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_send_24)
            setOnClickListener { onSendButtonClicked() }
        }
        setUpDropDownMenu()
    }

    private fun setUpDropDownMenu(){
        val items = listOf("Co-Administrador/a", "Empleado/a")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, items)
        (binding.employeeRole.editText as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun onSendButtonClicked(){
        if (checkIfEmailIsValid() && checkIfRoleIsValid()){
            binding.progressBar.visible()
            lifecycleScope.launch {
                when(viewModel.checkIfUserExists(binding.employeeEmailText.getString())){
                    SimpleResult.Success -> {
                        binding.progressBar.gone()
                        showLongSnackBarAboveFab("Se envio correctamente la solicitud.")
                    }
                    SimpleResult.Failure -> {
                        binding.progressBar.gone()
                        showLongSnackBarAboveFab("Hubo un problema al enviar la solicitud.")
                    }
                }
            }
        }
    }

    private fun checkIfEmailIsValid():Boolean{
        return when (val result = StringValidator.from(binding.employeeEmailText.getString(),allowSpecialChars = true)){
            is StringValidator.NotValid -> {
                showLongSnackBarAboveFab("El email no puede estar vacio.")
                false
            }
            is StringValidator.Valid -> true
        }
    }

    private fun checkIfRoleIsValid():Boolean{
        return if (binding.employeeRoleText.getString().isNotEmpty()){
            true
        }else{
            showLongSnackBarAboveFab("El rol no puede estar vacio.")
            false
        }
    }
}