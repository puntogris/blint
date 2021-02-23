package com.puntogris.blint.ui.business

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentAddBusinessEmployeeBinding
import com.puntogris.blint.model.EmployeeRequest
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddBusinessEmployee : BaseFragment<FragmentAddBusinessEmployeeBinding>(R.layout.fragment_add_business_employee) {

    private val viewModel: BusinessViewModel by viewModels()
    private val args:AddBusinessEmployeeArgs by navArgs()
    private var role = ""
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
        binding.employeeRoleText.setOnItemClickListener { _, _, i, _ ->
            when(i){
                0 -> role = "CO_OWNER"
                1 -> role = "EMPLOYEE"
            }
        }
    }

    private fun onSendButtonClicked(){
        if (checkIfEmailIsValid() && checkIfRoleIsValid()){
            lifecycleScope.launch {
                val request = EmployeeRequest(
                    email = binding.employeeEmailText.getString(),
                    businessName = args.business.businessName,
                    businessId = args.business.businessId,
                    role = role,
                    businessTimestamp = args.business.businessTimestamp)
                viewModel.sendRequest(request).collect {
                    when(it){
                        RequestResult.Error -> {
                            showLongSnackBarAboveFab("Ocurrio un error al procesar la solicitud. Intente nuevamente.")
                            binding.progressBar.gone()
                        }
                        RequestResult.InProgress -> {
                            binding.progressBar.visible()
                        }
                        RequestResult.NotFound -> {
                            showLongSnackBarAboveFab("No se encontro un usuario con ese mail.")
                            binding.progressBar.gone()
                        }
                        RequestResult.Success -> {
                            showLongSnackBarAboveFab("Se envio correctamente la solicitud. Se procesara en unos minutos.")
                            binding.progressBar.gone()
                        }
                    }
                }
            }
        }
    }

    private fun checkIfEmailIsValid(): Boolean{
        return when (val result = StringValidator.from(binding.employeeEmailText.getString(), allowSpecialChars = true)){
            is StringValidator.NotValid -> {
                showLongSnackBarAboveFab("El email no puede estar vacio.")
                false
            }
            is StringValidator.Valid -> true
        }
    }

    private fun checkIfRoleIsValid(): Boolean{
        return if (role.isNotEmpty()){
            true
        }else{
            showLongSnackBarAboveFab("El rol no puede estar vacio.")
            false
        }
    }
}