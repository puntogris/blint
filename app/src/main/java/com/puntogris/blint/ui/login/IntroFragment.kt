package com.puntogris.blint.ui.login

import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentIntroBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.TERMS_AND_CONDITIONS_URI
import com.puntogris.blint.utils.getString
import com.puntogris.blint.utils.setUpUi
import com.puntogris.blint.utils.setupStatusBarForLoginBackground
import com.puntogris.blint.utils.showShortSnackBar
import com.ybs.countrypicker.CountryPicker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroFragment: BaseFragment<FragmentIntroBinding>(R.layout.fragment_intro) {

    private var country = ""

    override fun initializeViews() {
        binding.fragment = this
        setUpUi(showFab = false, showAppBar = false, showToolbar = false)
        setupStatusBarForLoginBackground()
    }

    fun onReadMoreAboutPolicesClicked(){
        val action = IntroFragmentDirections.actionIntroFragmentToWebPageFragment(TERMS_AND_CONDITIONS_URI)
        findNavController().navigate(action)
    }

    fun onExitButtonClicked(){
        findNavController().navigate(R.id.loginFragment)
    }

    fun onGoToSyncUserBusiness(){
        val username = binding.usernameText.getString()
        if (!isCheckBoxChecked()){
            showShortSnackBar(getString(R.string.snack_error_connection_server_try_later))
        }else if (country.isEmpty()){
            showShortSnackBar(getString(R.string.snack_country_can_not_be_empty))
        }else if(username.isBlank() || username.length < 3){
            showShortSnackBar(getString(R.string.snack_name_can_not_be_empty))
        }else{
            val action = IntroFragmentDirections.actionIntroFragmentToFirstSyncFragment(
                username = username,
                userCountry = country
            )
            findNavController().navigate(action)
        }
    }

    fun onSelectCountryClicked(){
        val picker = CountryPicker.newInstance(getString(R.string.select_country))
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
            showShortSnackBar(getString(R.string.snack_accept_our_conditions_to_continue))
            false
        }
    }
}