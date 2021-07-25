package com.puntogris.blint.ui.orders.detailed_order

import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.DetailedOrderGraphNavDirections
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentPublishOrderBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PublishOrderFragment : BaseFragment<FragmentPublishOrderBinding>(R.layout.fragment_publish_order) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }
    private var isOperationInProgress = true

    override fun initializeViews() {
        registerUiInterface.register(showFab = true, showAppBar = false, showFabCenter = false, fabIcon = R.drawable.ic_baseline_arrow_forward_24){
            if (isOperationInProgress){
                showSnackBarVisibilityAppBar(getString(R.string.snack_wait_for_operation))
            }else{
                val nav = NavOptions.Builder().setPopUpTo(R.id.navigation, true).build()
                findNavController().navigate(R.id.mainFragment, null, nav)
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
        showSnackBarVisibilityAppBar(getString(R.string.snack_created_order_success))
        binding.title.text = getString(R.string.created_successfully_title)
        binding.subtitle.text = getString(R.string.order_create_success_message)
        binding.animationView.playAnimationOnce(R.raw.done)
    }

    private fun onPublishOrderFailureUi(){
        isOperationInProgress = false
        binding.title.text = getString(R.string.created_failed)
        binding.subtitle.text = getString(R.string.order_create_error_message)
        showSnackBarVisibilityAppBar(getString(R.string.snack_order_created_error))
        binding.animationView.playAnimationOnce(R.raw.error)
    }
}