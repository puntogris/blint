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
import com.puntogris.blint.databinding.FragmentCreateBackUpBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateBackupFragment : Fragment(R.layout.fragment_create_back_up) {

    private val viewModel: BackupViewModel by viewModels()

    private val binding  by viewBinding(FragmentCreateBackUpBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbarBackButton(binding.toolbar)
        subscribeUi()
        setupListeners()
    }

    private fun setupListeners() {
        binding.buttonCreateBackup.setOnClickListener {
            onBackupButtonClicked()
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
                            textViewBackupTitle.setText(R.string.create_backup_success_title)
                            textViewBackupSummary.setText(R.string.create_backup_success_message)
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
                            textViewLastBackup.setDateOrError(it.lastBackupDate)
                        }
                    }
                }
            }
        }
    }

    private fun onBackupButtonClicked() {
        InfoSheet().show(requireParentFragment().requireContext()) {
            title(R.string.ask_user_action_confirmation)
            content(R.string.create_backup_warning)
            onNegative(R.string.action_cancel)
            onPositive(R.string.action_create_backup) { viewModel.backupBusiness() }
        }
    }
}
