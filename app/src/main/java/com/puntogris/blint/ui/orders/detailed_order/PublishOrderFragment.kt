package com.puntogris.blint.ui.orders.detailed_order

import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentPublishOrderBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.playAnimationInfinite
import com.puntogris.blint.utils.playAnimationOnce
import com.puntogris.blint.utils.types.RepoResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PublishOrderFragment :
    BaseFragment<FragmentPublishOrderBinding>(R.layout.fragment_publish_order) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }

    override fun initializeViews() {
        UiInterface.registerUi(showAppBar = false)
        subscribeUi()
    }

    private fun subscribeUi() {
        lifecycleScope.launchWhenStarted {
            viewModel.publishOrder().collect {
                with(binding) {
                    when (it) {
                        is RepoResult.Error -> {
                            animationView.playAnimationOnce(R.raw.error)
                            publishOrderTitle.setText(R.string.created_failed)
                            publishOrderSubtitle.setText(R.string.order_create_error_message)
                            UiInterface.showSnackBar(getString(R.string.snack_order_created_error))
                        }
                        is RepoResult.Success -> {
                            animationView.playAnimationOnce(R.raw.done)
                            publishOrderTitle.setText(R.string.created_successfully_title)
                            publishOrderSubtitle.setText(R.string.order_create_success_message)
                            UiInterface.showSnackBar(getString(R.string.snack_created_order_success))
                        }
                        is RepoResult.InProgress -> {
                            animationView.playAnimationInfinite(R.raw.loading)
                        }
                    }
                    continueButton.isEnabled = it !is RepoResult.InProgress
                }
            }
        }
    }

    fun navigateToHome() {
        val nav = NavOptions.Builder().setPopUpTo(R.id.navigation, true).build()
        findNavController().navigate(R.id.mainFragment, null, nav)
    }
}