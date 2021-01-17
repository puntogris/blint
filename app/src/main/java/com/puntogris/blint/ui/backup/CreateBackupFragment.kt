package com.puntogris.blint.ui.backup

import androidx.fragment.app.viewModels
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentCreateBackUpBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.settings.PreferencesViewModel
import com.puntogris.blint.utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateBackupFragment : BaseFragment<FragmentCreateBackUpBinding>(R.layout.fragment_create_back_up) {

    private val viewModel: PreferencesViewModel by viewModels()

    override fun initializeViews() {

        val path = requireActivity().getDatabasePath("products_table").absolutePath

        binding.button7.setOnClickListener {
            binding.progressBar2.visible()
            viewModel.backupBusiness(listOf(path))
        }

    }
}