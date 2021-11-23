package com.puntogris.blint.ui.backup

import androidx.fragment.app.viewModels
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentRestoreBackupBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.types.BackupState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class RestoreBackupFragment :
    BaseFragment<FragmentRestoreBackupBinding>(R.layout.fragment_restore_backup) {

    private val viewModel: BackupViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        UiInterface.registerUi(showAppBar = false)
        subscribeUi()
    }

    private fun subscribeUi() {
        launchAndRepeatWithViewLifecycle {
            viewModel.backupState.collect {
                with(binding) {
                    when (it) {
                        is BackupState.Loading -> {
                            animationView.playAnimationInfinite(R.raw.loading)
                            backupTitle.setText(R.string.loading_with_dots)
                            backupSummary.setText(R.string.connecting_in_progress)
                            lastBackupGroup.gone()
                        }
                        is BackupState.BackupSuccess -> {
                            backupSummary.setText(R.string.restore_backup_success_message)
                            backupTitle.setText(R.string.restore_backup_success_title)
                            animationView.playAnimationOnce(R.raw.done)
                        }
                        is BackupState.Error -> {
                            backupTitle.setText(R.string.snack_an_error_occurred)
                            backupSummary.setText(it.error)
                            animationView.playAnimationOnce(R.raw.error)
                        }
                        is BackupState.ShowLastBackup -> {
                            backupTitle.setText(R.string.your_account_backups)
                            backupSummary.setText(R.string.data_from_servers)
                            lastBackupGroup.visible()
                            animationView.gone()
                            textView71.setDateOrError(it.lastBackupDate)
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
            onPositive(R.string.action_restore_backup)
            { viewModel.restoreBackup(getDatabasePath()) }
        }
    }

    fun onReadMoreAboutBackupsClicked() {
        launchWebBrowserIntent(Constants.BACKUP_WEBSITE_LEARN_MORE)
    }
}