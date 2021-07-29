package com.puntogris.blint.ui.backup

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentCreateBackUpBinding
import com.puntogris.blint.model.Business
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.settings.PreferencesViewModel
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.BACKUP_WEBSITE_LEARN_MORE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class CreateBackupFragment : BaseFragment<FragmentCreateBackUpBinding>(R.layout.fragment_create_back_up) {

    private val viewModel: PreferencesViewModel by viewModels()
    private lateinit var backupAdapter: BackupAdapter

    override fun initializeViews() {
        binding.fragment = this
        UiInterface.register(showAppBar = false)
        setUpRecyclerView()
        getUserBusiness()

        launchAndRepeatWithViewLifecycle {
            viewModel.getBackUpRequirements().collect {
                when(it){
                    is RepoResult.Success -> showBusinessUI(it.data)
                    is RepoResult.Error -> showNoBusinessFoundUI()
                    RepoResult.InProgress -> binding.loadingBusinessProgressBar.visible()
                }
            }
        }
    }

    private fun getUserBusiness(){

    }

    fun onReadMoreAboutBackupsClicked(){
        launchWebBrowserIntent(BACKUP_WEBSITE_LEARN_MORE)
    }

    fun onBackupButtonClicked(){
        showConfirmationDialogForBackUp()
    }

    private fun showNoBusinessFoundUI(){
        binding.loadingBusinessProgressBar.gone()
        binding.loadingBusinessSummary.text = getString(R.string.local_business_not_found_in_account)
    }

    private fun showBusinessUI(data: List<Business>){
        getLastBackUpDate()
        backupAdapter.submitList(data)
        binding.apply {
            loadingBusinessProgressBar.gone()
            loadingBusinessSummary.gone()
            businessTitle.visible()
            recyclerView.visible()
            button19.visible()
            textView69.visible()
            textView70.visible()
            textView71.visible()
        }
    }

    private fun getLastBackUpDate(){
        lifecycleScope.launchWhenStarted {
            viewModel.getLastBackUpDate().collect {
                when(it){
                    is RepoResult.Error -> {
                        binding.textView71.text = getString(R.string.not_found)
                    }
                    RepoResult.InProgress -> {}
                    is RepoResult.Success -> {
                        binding.textView71.text = Date(it.data).getDateFormattedString()
                    }
                }
            }
        }
    }

    private fun showBackupInProgressUI(){
        binding.apply {
            animationView.repeatCount = LottieDrawable.INFINITE
            animationView.visible()
            animationView.playAnimation()
            recyclerView.gone()
            businessTitle.gone()
            button19.gone()
            textView69.gone()
            textView70.gone()
            textView71.gone()
            businessTitle.visible()
            businessTitle.text = getString(R.string.create_backup_in_progress)
            backupSummary.text = getString(R.string.do_not_close_window_until_backup_is_done)
        }
    }

    private fun setUpRecyclerView(){
        binding.recyclerView.apply {
            backupAdapter = BackupAdapter()
            adapter = backupAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showConfirmationDialogForBackUp(){
        InfoSheet().show(requireParentFragment().requireContext()){
            title(this@CreateBackupFragment.getString(R.string.ask_user_action_confirmation))
            content(this@CreateBackupFragment.getString(R.string.create_backup_warning))
            onNegative(this@CreateBackupFragment.getString(R.string.action_cancel))
            onPositive(this@CreateBackupFragment.getString(R.string.action_create_backup))
            { startBusinessBackup() }
        }
    }

    private fun startBusinessBackup(){
        lifecycleScope.launch {
            viewModel.backupBusiness(getDatabasePath()).collectLatest {
                when(it){
                    is BackupState.Error -> showFailureBackupUI()
                    is BackupState.InProgress -> showBackupInProgressUI()
                    BackupState.Success -> showSuccessfulBackupUI()
                }
            }
        }
    }

    private fun showSuccessfulBackupUI(){
        binding.backupSummary.text = getString(R.string.create_backup_success_message)
        binding.businessTitle.text = getString(R.string.create_backup_success_title)
        binding.animationView.playAnimationOnce(R.raw.done)
    }

    private fun showFailureBackupUI(){
        binding.businessTitle.text = getString(R.string.create_backup_error_title)
        binding.backupSummary.text = getString(R.string.create_backup_error_message)
        binding.animationView.playAnimationOnce(R.raw.error)
    }
}