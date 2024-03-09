package com.puntogris.blint.feature_store.presentation.backup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.gone
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.launchWebBrowserIntent
import com.puntogris.blint.common.utils.playAnimationInfinite
import com.puntogris.blint.common.utils.playAnimationOnce
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.setDateOrError
import com.puntogris.blint.common.utils.types.BackupState
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.common.utils.visible
import com.puntogris.blint.databinding.FragmentRestoreBackupBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestoreBackupFragment : Fragment(R.layout.fragment_restore_backup) {

    private val viewModel: BackupViewModel by viewModels()

    private val binding by viewBinding(FragmentRestoreBackupBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeUi()
        setupListeners()
        registerToolbarBackButton(binding.toolbar)
    }

    private fun setupListeners() {
        binding.buttonRestore.setOnClickListener {
            onRestorationButtonClicked()
        }
        binding.textViewReadMore.setOnClickListener {
            launchWebBrowserIntent(Constants.BACKUP_LEARN_MORE_URL)
        }
    }

    private fun subscribeUi() {
        launchAndRepeatWithViewLifecycle {
            viewModel.backupState.collect {
                with(binding) {
                    when (it) {
                        is BackupState.Loading -> {
                            viewAnimation.playAnimationInfinite(R.raw.loading)
                            textViewBackupTitle.setText(R.string.loading_with_dots)
                            textViewBackupSummary.setText(R.string.connecting_in_progress)
                            groupLastBackup.gone()
                        }

                        is BackupState.BackupSuccess -> {
                            textViewBackupSummary.setText(R.string.restore_backup_success_message)
                            textViewBackupTitle.setText(R.string.restore_backup_success_title)
                            viewAnimation.playAnimationOnce(R.raw.done)
                        }

                        is BackupState.Error -> {
                            textViewBackupTitle.setText(R.string.snack_an_error_occurred)
                            textViewBackupSummary.setText(it.error)
                            viewAnimation.playAnimationOnce(R.raw.error)
                        }

                        is BackupState.ShowLastBackup -> {
                            textViewBackupTitle.setText(R.string.your_account_backups)
                            textViewBackupSummary.setText(R.string.data_from_servers)
                            groupLastBackup.visible()
                            viewAnimation.gone()
                            textViewLastBackupDate.setDateOrError(it.lastBackupDate)
                        }
                    }
                }
            }
        }
    }

    fun onRestorationButtonClicked() {
        InfoSheet().show(requireParentFragment().requireContext()) {
            title(R.string.ask_user_action_confirmation)
            content(R.string.restore_backup_warning)
            onNegative(R.string.action_cancel)
            onPositive(R.string.action_restore_backup) { viewModel.restoreBackup() }
        }
    }
}
