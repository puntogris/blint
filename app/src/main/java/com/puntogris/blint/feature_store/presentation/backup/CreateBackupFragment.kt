package com.puntogris.blint.feature_store.presentation.backup

import androidx.fragment.app.viewModels
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.*
import com.puntogris.blint.common.utils.types.BackupState
import com.puntogris.blint.databinding.FragmentCreateBackUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CreateBackupFragment :
    BaseFragment<FragmentCreateBackUpBinding>(R.layout.fragment_create_back_up) {

    private val viewModel: BackupViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
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
                            backupSummary.setText(R.string.create_backup_success_message)
                            backupTitle.setText(R.string.create_backup_success_title)
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