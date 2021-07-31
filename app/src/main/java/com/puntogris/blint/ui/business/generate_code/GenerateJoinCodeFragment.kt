package com.puntogris.blint.ui.business.generate_code

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentGenerateJoinCodeBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenerateJoinCodeFragment : BaseFragment<FragmentGenerateJoinCodeBinding>(R.layout.fragment_generate_join_code) {

    private val viewModel: GenerateJoinCodeViewModel by viewModels()
    private val args: GenerateJoinCodeFragmentArgs by navArgs()

    override fun initializeViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.fragment = this
        
        lifecycleScope.launchWhenStarted {
            when(val result = viewModel.fetchJoiningCode(args.business.businessId)){
                is RepoResult.Error -> {
                    binding.loadingGroup.gone()
                    binding.addEmployeeTitle.text = getString(R.string.snack_an_error_occurred)
                    binding.addEmployeeSummary.text = getString(R.string.snack_error_connection_server_try_later)
                }
                is RepoResult.Success -> {
                    binding.loadedGroup.visible()
                    binding.loadingGroup.gone()
                    binding.addEmployeeSummary.visible()
                    binding.joinBusinessCode.text = result.data.codeId
                    binding.addEmployeeTitle.text = getString(R.string.code_generated)
                    val bitmap = generateQRImage(result.data.codeId, 700, 700)
                    binding.qrCodeImage.setImageBitmap(bitmap)
                    viewModel.codeExpirationCountDown(result.data.timestamp)
                }
            }
        }
    }

    fun onCopyCodeClicked(){
        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("simple text", binding.joinBusinessCode.text)
        clipboard.setPrimaryClip(clip)
        UiInterface.showSnackBar(getString(R.string.copied_to_clipboard))
    }

    fun onShareCodeClicked(){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, binding.joinBusinessCode.text.toString())
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.join_my_business_with_code))
        startActivity(shareIntent)
    }

    override fun onDestroyView() {
        viewModel.cancelExpirationTimer()
        super.onDestroyView()
    }
}