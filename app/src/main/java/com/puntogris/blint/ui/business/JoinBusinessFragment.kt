package com.puntogris.blint.ui.business

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentJoinBusinessBinding
import com.puntogris.blint.model.Employee
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.login.LoginViewModel
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class JoinBusinessFragment : BaseFragment<FragmentJoinBusinessBinding>(R.layout.fragment_join_business) {

    private val viewModel: BusinessViewModel by viewModels()
    @Inject lateinit var sharedPref: SharedPref

    override fun initializeViews() {
        binding.fragment = this
        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_check_24)
            setOnClickListener {
                val text = binding.joinBusinessCodeText.getString()
                if(text.isEmpty()){
                    showLongSnackBarAboveFab("Ingresa un codigo o escanea el mismo para continuar.")
                }else{
                    joinBusinessWithCode(text)
                }
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("key")?.observe(
            viewLifecycleOwner) {
            joinBusinessWithCode(it)
        }
    }

    private fun joinBusinessWithCode(code:String){
        getParentFab().hide()
        binding.setDataGroup.gone()
        binding.animationView.visible()
        binding.summaryMessage.visible()
        binding.fragmentTitle.text = "Uniendote al negocio.."
        binding.animationView.apply {
            repeatCount = LottieDrawable.INFINITE
            visible()
            playAnimation()
        }
        lifecycleScope.launch {
            when(viewModel.createEmployee(code)){
                JoinBusiness.Error -> {
                    binding.animationView.apply {
                        setAnimation(R.raw.error)
                        repeatCount = 0
                        playAnimation()
                    }
                    binding.fragmentTitle.text = "Encontramos un Error"
                    binding.summaryMessage.text = "Hubo un problema conectandose con los servidores. Intenta nuevamente."
                }
                JoinBusiness.InProgress -> {}
                JoinBusiness.CodeInvalid -> {
                    binding.animationView.apply {
                        setAnimation(R.raw.error)
                        repeatCount = 0
                        playAnimation()
                    }
                    binding.fragmentTitle.text = "Codigo incorrecto"
                    binding.summaryMessage.text = "El codigo es incorrecto o expiro, pidele al administrador/a que verifique la validez del mismo."
                }
                JoinBusiness.Success -> {
                    sharedPref.setUserHasBusinessPref(true)
                    binding.animationView.apply {
                        binding.animationView.apply {
                            setAnimation(R.raw.done)
                            repeatCount = 0
                            playAnimation()
                        }
                        setAnimation(R.raw.done)
                        repeatCount = 0
                        playAnimation()
                    }
                }
                JoinBusiness.AlreadyJoined -> {
                    binding.animationView.apply {
                        setAnimation(R.raw.error)
                        repeatCount = 0
                        playAnimation()
                    }
                    binding.fragmentTitle.text = "Conflicto de empleados"
                    binding.summaryMessage.text = "Esta cuenta ya se encuentra enlazada en este negocio."
                }
            }
        }
    }

    fun onScannerButtonClicked(){
        val action = JoinBusinessFragmentDirections.actionJoinBusinessFragmentToScannerFragment(1)
        findNavController().navigate(action)
    }

}