package com.puntogris.blint.ui

import android.util.Size
import android.view.*
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentScannerBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.BarcodeAnalyzer
import com.puntogris.blint.utils.changeIconFromDrawable
import com.puntogris.blint.utils.getParentFab
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class ScannerFragment : BaseFragment<FragmentScannerBinding>(R.layout.fragment_scanner) {

    @Inject lateinit var barcodeAnalyzer: BarcodeAnalyzer
    private lateinit var preview: Preview
    private lateinit var camera: Camera
    private lateinit var cameraExecutor: ExecutorService
    private var flashEnabled = false

    override fun initializeViews() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()
        binding.overlay.post {
            binding.overlay.setViewFinder()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val rotation = binding.viewFinder.display.rotation

            preview = Preview.Builder()
                    .build()

       //     val metrics = DisplayMetrics().also { binding.viewFinder.display.getRealMetrics(it) }

            val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetRotation(rotation)
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { it.setAnalyzer(cameraExecutor, barcodeAnalyzer) }

            val cameraSelector: CameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

            val orientationEventListener = object : OrientationEventListener(requireContext()) {
                override fun onOrientationChanged(orientation : Int) {
                     when (orientation) {
                        in 45..134 -> Surface.ROTATION_270
                        in 135..224 -> Surface.ROTATION_180
                        in 225..314 -> Surface.ROTATION_90
                        else -> Surface.ROTATION_0
                    }.also { imageAnalysis.targetRotation = it}
                }
            }
            orientationEventListener.enable()

            barcodeAnalyzer.onResult {
                requireActivity().runOnUiThread {
                    imageAnalysis.clearAnalyzer()
                    cameraProvider.unbindAll()
                    ScannerResultDialog
                        .newInstance(it, object : ScannerResultDialog.DialogDismissListener {
                            override fun onDismiss() {
                                startCamera()
                            }
                        })
                        .show(parentFragmentManager, ScannerResultDialog::class.java.simpleName)
                }
            }

            try {
                cameraProvider.unbindAll()
                preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, imageAnalysis, preview)
                val parentFab = getParentFab()
                if (camera.cameraInfo.hasFlashUnit()) {
                    parentFab.changeIconFromDrawable(R.drawable.ic_baseline_flash_off_24)
                    parentFab.setOnClickListener {
                        camera.cameraControl.enableTorch(!flashEnabled)
                    }
                    camera.cameraInfo.torchState.observe(viewLifecycleOwner) {
                        it?.let { torchState ->
                            if (torchState == TorchState.ON) {
                                flashEnabled = true
                                parentFab.changeIconFromDrawable(R.drawable.ic_baseline_flash_on_24)
                            } else {
                                flashEnabled = false
                                parentFab.changeIconFromDrawable(R.drawable.ic_baseline_flash_off_24)
                            }
                        }
                    }
                }

            } catch(exc: Exception) { Timber.e(exc, "Use case binding failed") }

        }, ContextCompat.getMainExecutor(requireContext()))
    }
}