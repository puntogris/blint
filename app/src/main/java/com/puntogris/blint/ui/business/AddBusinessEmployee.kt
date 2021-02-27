package com.puntogris.blint.ui.business

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.zxing.WriterException
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentAddBusinessEmployeeBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddBusinessEmployee : BaseFragment<FragmentAddBusinessEmployeeBinding>(R.layout.fragment_add_business_employee) {

    private val viewModel: BusinessViewModel by viewModels()
    private val args: AddBusinessEmployeeArgs by navArgs()

    override fun initializeViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.fragment = this
        lifecycleScope.launchWhenStarted {
            when(val result = viewModel.fetchJoiningCode(args.business.businessId)){
                is RepoResult.Error -> {
                    binding.loadingGroup.gone()
                }
                is RepoResult.Success -> {
                    binding.loadedGroup.visible()
                    binding.loadingGroup.gone()
                    binding.addEmployeeSummary.visible()
                    binding.joinBusinessCode.text = result.data.id
                    binding.addEmployeeTitle.text = "Codigo generado."
                    generateQRImage(result.data.id)
                    viewModel.codeExpirationCountDown(result.data.timestamp)
                }
            }
        }
    }

    private fun generateQRImage(code: String){
        val qrgEncoder = QRGEncoder(code, null, QRGContents.Type.TEXT, 300)
        qrgEncoder.colorBlack = Color.WHITE
        qrgEncoder.colorWhite = ContextCompat.getColor(requireContext(), R.color.nightBackground)
        try {
            val bitmap = qrgEncoder.bitmap
            binding.qrCodeImage.setImageBitmap(bitmap)
        } catch (e: WriterException) {

        }
    }

    fun onCopyCodeClicked(){
        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("simple text", binding.joinBusinessCode.text)
        clipboard.setPrimaryClip(clip)
        showLongSnackBarAboveFab("Codigo copiado al portapapeles.")
    }

    fun onShareCodeClicked(){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, binding.joinBusinessCode.text.toString())
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Unite a mi negocio en Blint con este codigo!")
        startActivity(shareIntent)
    }

    override fun onDestroyView() {
        viewModel.cancelExpirationTimer()
        super.onDestroyView()
    }
}