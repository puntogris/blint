package com.puntogris.blint.feature_store.presentation.sync

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.navigateAndClearStack
import com.puntogris.blint.common.utils.playAnimationOnce
import com.puntogris.blint.common.utils.types.SyncAccount
import com.puntogris.blint.databinding.FragmentSyncAccountBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SyncAccountFragment :
    BaseFragment<FragmentSyncAccountBinding>(R.layout.fragment_sync_account) {

    private val viewModel: SyncViewModel by viewModels()

    override fun initializeViews() {
        subscribeUi()
    }

    private fun subscribeUi() {
        launchAndRepeatWithViewLifecycle(Lifecycle.State.CREATED) {
            when (viewModel.syncAccount()) {
                is SyncAccount.Error -> onErrorSync()
                SyncAccount.Success.BusinessNotFound -> onSuccessSync(R.id.newUserFragment)
                SyncAccount.Success.HasBusiness -> onSuccessSync(R.id.homeFragment)
            }
        }
    }

    private fun onErrorSync() {
        with(binding) {
            viewAnimation.playAnimationOnce(R.raw.error)
            textViewSyncTitle.setText(R.string.snack_an_error_occurred)
            textViewSyncSubtitle.setText(R.string.snack_sync_account_error)
        }
        with(binding.buttonContinue) {
            isEnabled = true
            setText(R.string.action_exit)
            setOnClickListener {
                findNavController().navigateAndClearStack(R.id.loginFragment)
            }
        }
    }

    private fun onSuccessSync(destination: Int) {
        with(binding) {
            viewAnimation.playAnimationOnce(R.raw.done)
            textViewSyncTitle.setText(R.string.account_sync_success)
            textViewSyncSubtitle.setText(R.string.account_adventure_message)
            buttonContinue.isEnabled = true
            buttonContinue.setOnClickListener {
                findNavController().navigateAndClearStack(destination)
            }
        }
    }
}
