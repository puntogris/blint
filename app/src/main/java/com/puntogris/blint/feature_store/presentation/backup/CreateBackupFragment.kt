package com.puntogris.blint.feature_store.presentation.backup

import androidx.fragment.app.viewModels
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.*
import com.puntogris.blint.common.utils.types.BackupState
import com.puntogris.blint.databinding.FragmentCreateBackUpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateBackupFragment :
    BaseFragment<FragmentCreateBackUpBinding>(R.layout.fragment_create_back_up) {

    private val viewModel: BackupViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        subscribeUi()
        registerToolbarBackButton(binding.createBackupToolbar)
    }

    private fun subscribeUi() {
        launchAndRepeatWithViewLifecycle {
            viewModel.backupState.collect {
                with(binding) {
                    when (it) {
                        is BackupState.Loading -> {
                            createBackupAnimationView.playAnimationInfinite(R.raw.loading)
                            createBackupTitle.setText(R.string.loading_with_dots)
                            createBackupSummary.setText(R.string.connecting_in_progress)
                            createBackupLastBackupGroup.gone()
                        }
                        is BackupState.BackupSuccess -> {
                            createBackupTitle.setText(R.string.create_backup_success_title)
                            createBackupSummary.setText(R.string.create_backup_success_message)
                            createBackupAnimationView.playAnimationOnce(R.raw.done)
                        }
                        is BackupState.Error -> {
                            createBackupTitle.setText(R.string.snack_an_error_occurred)
                            createBackupSummary.setText(it.error)
                            createBackupAnimationView.playAnimationOnce(R.raw.error)
                        }
                        is BackupState.ShowLastBackup -> {
                            createBackupTitle.setText(R.string.your_account_backups)
                            createBackupSummary.setText(R.string.data_from_servers)
                            createBackupLastBackupGroup.visible()
                            createBackupAnimationView.gone()
                            createBackupLastBackup.setDateOrError(it.lastBackupDate)
                        }
                    }
                }
            }
        }
    }

    fun onBackupButtonClicked() {
        InfoSheet().show(requireParentFragment().requireContext()) {
            title(R.string.ask_user_action_confirmation)
            content(R.string.create_backup_warning)
            onNegative(R.string.action_cancel)
            onPositive(R.string.action_create_backup) {
                viewModel.backupBusiness()
            }
        }
    }

    fun onReadMoreAboutBackupsClicked() {
        launchWebBrowserIntent(Constants.BACKUP_LEARN_MORE_URL)
    }
}