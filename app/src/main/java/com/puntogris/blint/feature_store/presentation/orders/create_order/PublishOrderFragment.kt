package com.puntogris.blint.feature_store.presentation.orders.create_order

import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.navigateAndClearStack
import com.puntogris.blint.common.utils.playAnimationInfinite
import com.puntogris.blint.common.utils.playAnimationOnce
import com.puntogris.blint.common.utils.types.ProgressResource
import com.puntogris.blint.databinding.FragmentPublishOrderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PublishOrderFragment :
    BaseFragment<FragmentPublishOrderBinding>(R.layout.fragment_publish_order) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.createOrderGraphNav) { defaultViewModelProviderFactory }

    override fun initializeViews() {
        binding.fragment = this
        subscribeUi()
    }

    private fun subscribeUi() {
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.publishOrder().collect {
                with(binding) {
                    when (it) {
                        is ProgressResource.Error -> {
                            publishOrderAnimationView.playAnimationOnce(R.raw.error)
                            publishOrderTitle.setText(R.string.created_failed)
                            publishOrderSubtitle.setText(R.string.order_create_error_message)
                            UiInterface.showSnackBar(getString(it.error))
                        }

                        is ProgressResource.Success -> {
                            publishOrderAnimationView.playAnimationOnce(R.raw.done)
                            publishOrderTitle.setText(R.string.created_successfully_title)
                            publishOrderSubtitle.setText(R.string.order_create_success_message)
                        }

                        is ProgressResource.InProgress -> {
                            publishOrderAnimationView.playAnimationInfinite(R.raw.loading)
                        }
                    }
                    publishOrderContinueButton.isEnabled = it !is ProgressResource.InProgress
                }
            }
        }
    }

    fun navigateToHome() {
        findNavController().navigateAndClearStack(R.id.homeFragment)
    }
}
