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
        binding.fragment = this
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
            syncAccountAnimationView.playAnimationOnce(R.raw.error)

            syncAccountTitle.setText(R.string.snack_an_error_occurred)
            syncAccountSubtitle.setText(R.string.snack_sync_account_error)

            syncAccountContinueButton.isEnabled = true
            syncAccountContinueButton.setText(R.string.action_exit)
            syncAccountContinueButton.setOnClickListener {
                findNavController().navigateAndClearStack(R.id.loginFragment)
            }
        }
    }

    private fun onSuccessSync(destination: Int) {
        with(binding) {
            syncAccountAnimationView.playAnimationOnce(R.raw.done)

            syncAccountTitle.setText(R.string.account_sync_success)
            syncAccountSubtitle.setText(R.string.account_adventure_message)

            syncAccountContinueButton.isEnabled = true
            syncAccountContinueButton.setOnClickListener {
                findNavController().navigateAndClearStack(destination)
            }
        }
    }
}
