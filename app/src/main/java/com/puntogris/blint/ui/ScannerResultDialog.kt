package com.puntogris.blint.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.puntogris.blint.databinding.ScannerResultDialogBinding
import com.puntogris.blint.utils.Constants.ARG_SCANNING_RESULT

class ScannerResultDialog(private val listener: DialogDismissListener) : BottomSheetDialogFragment() {

    private lateinit var binding: ScannerResultDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScannerResultDialogBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannedResult = arguments?.getString(ARG_SCANNING_RESULT)
        binding.edtResult.setText(scannedResult)
        binding.btnCopy.setOnClickListener {
            //navigate to another fragment with result
        }
    }

    companion object {
        fun newInstance(scanningResult: String, listener: DialogDismissListener): ScannerResultDialog =
            ScannerResultDialog(listener).apply {
                arguments = Bundle().apply {
                    putString(ARG_SCANNING_RESULT, scanningResult)
                }
            }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onDismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener.onDismiss()
    }

    interface DialogDismissListener {
        fun onDismiss()
    }
}