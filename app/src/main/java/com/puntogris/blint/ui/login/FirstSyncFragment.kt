package com.puntogris.blint.ui.login

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentFirstSyncBinding
import com.puntogris.blint.model.Employee
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class FirstSyncFragment : BaseFragment<FragmentFirstSyncBinding>(R.layout.fragment_first_sync) {

    private val viewModel: LoginViewModel by viewModels()
    private val args: FirstSyncFragmentArgs by navArgs()
    @Inject lateinit var sharedPref: SharedPref

    override fun initializeViews() {
        binding.fragment = this
        setUpUi(showFab = false, showAppBar = false, showToolbar = false)
        setupStatusBarForLoginBackground()

        lifecycleScope.launchWhenStarted {
            viewModel.getUserBusiness().collect {
                when(it){
                    is UserBusiness.Error -> {
                        showLongSnackBarAboveFab(getString(R.string.snack_an_error_occurred))
                    }
                    UserBusiness.InProgress -> {}
                    UserBusiness.NotFound -> onEmployeeDataNotFound()
                    is UserBusiness.Success -> onEmployeeDataFound(it.data)
                }
            }
        }
    }

    fun onExitButtonClicked(){
        findNavController().navigate(R.id.loginFragment)
    }

    fun onContinueButtonClicked(){
        if (sharedPref.getUserHasBusinessPref()) findNavController().navigate(R.id.mainFragment)
        else findNavController().navigate(R.id.newUserFragment)
    }

    private fun onEmployeeDataFound(employees:List<Employee>){
        lifecycleScope.launch {
            when(viewModel.updateUserData(args.username, args.userCountry)){
                SimpleResult.Failure -> {
                    binding.animationView.apply {
                        setAnimation(R.raw.error)
                        repeatCount = 0
                        playAnimation()
                    }
                    binding.subtitle.text = getText(R.string.snack_error_connection_server_try_later)
                    binding.title.text = getString(R.string.snack_an_error_occurred)
                }
                SimpleResult.Success -> {
                    withContext(Dispatchers.IO){ viewModel.saveBusinessToLocalDatabase(employees, args.username, args.userCountry) }
                    sharedPref.setWelcomeUiPref(true)
                    sharedPref.setUserHasBusinessPref(true)
                    binding.animationView.apply {
                        setAnimation(R.raw.done)
                        repeatCount = 0
                        playAnimation()
                    }
                    binding.continueButton.isEnabled = true
                    binding.subtitle.text = getString(R.string.account_created_cheers_message)
                    binding.title.text = getString(R.string.account_created_success)
                }
            }
        }
    }

    private fun onEmployeeDataNotFound(){
        // crear una pantalla donde te haga crear o unirte o sino
        // podemos mostrar el inicio con un negocio vacio
        // o mostrar algo en ves del inicio y no dejando acceder a nada

//        lifecycleScope.launch {
//            viewModel.updateUserData(args.username, args.userCountry)
//            sharedPref.setWelcomeUiPref(true)
//            sharedPref.setUserHasBusinessPref(true)
//            withContext(Dispatchers.IO){viewModel.saveUserData(args.username, args.userCountry)}
//            withContext(Dispatchers.Main){
//                binding.animationView.apply {
//                    setAnimation(R.raw.done)
//                    repeatCount = 0
//                    playAnimation()
//                }
//                binding.continueButton.isEnabled = true
//                binding.subtitle.text = "Tu cuenta esta lista para arrancar esta nueva aventura!"
//                binding.title.text = "Cuenta creada correctamente."
//            }
//        }
    }
}