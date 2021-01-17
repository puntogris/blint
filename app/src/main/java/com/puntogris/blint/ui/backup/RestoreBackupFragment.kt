package com.puntogris.blint.ui.backup

import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentRestoreBackupBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.settings.PreferencesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestoreBackupFragment : BaseFragment<FragmentRestoreBackupBinding>(R.layout.fragment_restore_backup) {

    private val viewModel: PreferencesViewModel by viewModels()

    override fun initializeViews() {
//        binding.button8.setOnClickListener {
//            lifecycleScope.launch {
//                when(viewModel.restoreBackup()){
//                    RepoResult.Success -> println("yay")
//                    RepoResult.Failure -> println("no")
//                }
//            }
//        }
    }
}