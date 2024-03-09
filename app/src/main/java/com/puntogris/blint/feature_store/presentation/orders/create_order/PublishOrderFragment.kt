package com.puntogris.blint.feature_store.presentation.orders.create_order

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.navigateAndClearStack
import com.puntogris.blint.common.utils.playAnimationInfinite
import com.puntogris.blint.common.utils.playAnimationOnce
import com.puntogris.blint.common.utils.types.ProgressResource
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentPublishOrderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PublishOrderFragment : Fragment(R.layout.fragment_publish_order) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.createOrderGraphNav) {
        defaultViewModelProviderFactory
    }

    private val binding by viewBinding(FragmentPublishOrderBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        subscribeUi()
    }

    private fun setupListeners() {
        binding.buttonContinue.setOnClickListener {
            navigateToHome()
        }
    }

    private fun subscribeUi() {
        launchAndRepeatWithViewLifecycle(Lifecycle.State.STARTED) {
            viewModel.publishOrder().collect {
                with(binding) {
                    when (it) {
                        is ProgressResource.Error -> {
                            viewAnimation.playAnimationOnce(R.raw.error)
                            textViewPublishOrderSubtitle.setText(R.string.created_failed)
                            textViewPublishOrderSubtitle.setText(R.string.order_create_error_message)
                            UiInterface.showSnackBar(getString(it.error))
                        }

                        is ProgressResource.Success -> {
                            viewAnimation.playAnimationOnce(R.raw.done)
                            textViewPublishOrderTitle.setText(R.string.created_successfully_title)
                            textViewPublishOrderSubtitle.setText(R.string.order_create_success_message)
                        }

                        is ProgressResource.InProgress -> {
                            viewAnimation.playAnimationInfinite(R.raw.loading)
                        }
                    }
                    buttonContinue.isEnabled = it !is ProgressResource.InProgress
                }
            }
        }
    }

    fun navigateToHome() {
        findNavController().navigateAndClearStack(R.id.homeFragment)
    }
}
