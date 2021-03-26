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
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.UserBusiness
import com.puntogris.blint.utils.showLongSnackBarAboveFab
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class FirstSyncFragment : BaseFragment<FragmentFirstSyncBinding>(R.layout.fragment_first_sync) {

    private val viewModel: LoginViewModel by viewModels()
    @Inject
    lateinit var sharedPref: SharedPref

    private val args: FirstSyncFragmentArgs by navArgs()

    override fun initializeViews() {
        lifecycleScope.launch {
            viewModel.getUserBusiness().collect {
                when(it){
                    is UserBusiness.Error -> {
                        showLongSnackBarAboveFab("Se produjo un error.")
                    }
                    UserBusiness.InProgress -> {
                    }
                    UserBusiness.NotFound -> onEmployeeDataNotFound()
                    is UserBusiness.Success -> onEmployeeDataFound(it.data)
                }
            }
        }
        binding.button12.setOnClickListener {
            if (sharedPref.getUserHasBusinessPref()) findNavController().navigate(R.id.mainFragment)
            else findNavController().navigate(R.id.newUserFragment)
        }
        binding.button112.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
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
                    binding.textView107.text = "Hubo un problema al conectarnos con nuestros servidores. Intenta nuevamente.!"
                    binding.textView117.text = "Se encontro un error."
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
                    binding.button12.isEnabled = true
                    binding.textView107.text = "Tu cuenta esta lista para arrancar esta nueva aventura!"
                    binding.textView117.text = "Cuenta creada correctamente."
                }
            }
        }
    }

    private fun onEmployeeDataNotFound(){
        lifecycleScope.launch {
            viewModel.updateUserData(args.username, args.userCountry)
            sharedPref.setWelcomeUiPref(true)
            sharedPref.setUserHasBusinessPref(true)
            withContext(Dispatchers.IO){viewModel.saveUserData(args.username, args.userCountry)}
            withContext(Dispatchers.Main){
                binding.animationView.apply {
                    setAnimation(R.raw.done)
                    repeatCount = 0
                    playAnimation()
                }
                binding.button12.isEnabled = true
                binding.textView107.text = "Tu cuenta esta lista para arrancar esta nueva aventura!"
                binding.textView117.text = "Cuenta creada correctamente."
            }
        }
    }
}