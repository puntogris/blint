package com.puntogris.blint.ui.login

import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentIntroBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.getString
import com.puntogris.blint.utils.showShortSnackBar
import com.ybs.countrypicker.CountryPicker
import dagger.hilt.android.AndroidEntryPoint

class IntroFragment : BaseFragment<FragmentIntroBinding>(R.layout.fragment_intro) {

    private var country = ""

    override fun initializeViews() {
        binding.fragment = this

        binding.button112.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    fun onReadMoreAboutPolicesClicked(){
        findNavController().navigate(R.id.termsConditionsFragment)
    }


    fun onGoToSyncUserBusiness(){
        val username = binding.usernameText.getString()
        if (!isCheckBoxChecked()){
            showShortSnackBar("Es necesario que acepter los terminos y condiciones.")
        }else if (country.isEmpty()){
            showShortSnackBar("Necesitas seleccionar el pais de residencia.")
        }else if(username.isBlank() || username.length < 3){
            showShortSnackBar("El nombre no puede estar vacio ni ser menor de 3 caracteres.")
        }else{
            val action = IntroFragmentDirections.actionIntroFragmentToFirstSyncFragment(
                username = username,
                userCountry = country
            )
            findNavController().navigate(action)
        }
    }

    fun onSelectCountryClicked(){
        val picker = CountryPicker.newInstance("Seleccionar pais")
        picker.setListener { name, code, _, _ ->
            country = code
            binding.textView132.text = name
            picker.dismiss()
        }
        picker.show(parentFragmentManager, "COUNTRY_PICKER")
    }

    private fun isCheckBoxChecked(): Boolean{
        return if(binding.termsAndConditionsCheckBox.isChecked) true
        else{
            showShortSnackBar("Acepta nuestra condiciones para poder continuar.")
            false
        }
    }
}