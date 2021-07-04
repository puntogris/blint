package com.puntogris.blint.ui.orders.new_order

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentPublishOrderBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.orders.NewOrderViewModel
import com.puntogris.blint.utils.SimpleResult
import com.puntogris.blint.utils.setUpUi
import com.puntogris.blint.utils.showSnackBarVisibilityAppBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PublishOrderFragment : BaseFragment<FragmentPublishOrderBinding>(R.layout.fragment_publish_order) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.newOrderGraphNav) { defaultViewModelProviderFactory }
    private var isOperationInProgress = true

    override fun initializeViews() {
        setUpUi(showFab = true, showAppBar = false, showFabCenter = false, fabIcon = R.drawable.ic_baseline_arrow_forward_24){
            if (isOperationInProgress){
                showSnackBarVisibilityAppBar("Por favor espera que se termine la operacion para continuar.")
            }else{
                findNavController().navigate(R.id.mainFragment)
            }
        }
        lifecycleScope.launchWhenStarted {
            when(viewModel.publishOrderDatabase()){
                SimpleResult.Failure -> onPublishOrderFailureUi()
                SimpleResult.Success -> onPublishOrderSuccessUi()
            }
        }
    }

    private fun onPublishOrderSuccessUi(){
        isOperationInProgress = false
        showSnackBarVisibilityAppBar("Se creo correctamente la orden.")
        binding.title.text = "Creacion satisfactoria."
        binding.subtitle.text = "*Se creo correctamente la orden. Que tengas un muy buen dia!"
        binding.animationView.apply {
            setAnimation(R.raw.done)
            repeatCount = 0
            playAnimation()
        }
    }

    private fun onPublishOrderFailureUi(){
        isOperationInProgress = false
        binding.title.text = "Creacion fallida."
        binding.subtitle.text = "No se pudo crear tu orden. Revisa tu conexcion a internet y intenta nuevamente."
        showSnackBarVisibilityAppBar("Ocurrio un error creando tu orden. Intenta nuevamente")
        binding.animationView.apply {
            setAnimation(R.raw.error)
            repeatCount = 0
            playAnimation()
        }
    }
}