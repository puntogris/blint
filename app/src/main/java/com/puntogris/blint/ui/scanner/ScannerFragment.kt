package com.puntogris.blint.ui.scanner

import android.util.Size
import android.view.OrientationEventListener
import android.view.Surface
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.common.util.concurrent.ListenableFuture
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentScannerBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.BarcodeAnalyzer
import com.puntogris.blint.utils.Constants.PRODUCT_BARCODE_KEY
import com.puntogris.blint.utils.UiInterface
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class ScannerFragment : BaseFragment<FragmentScannerBinding>(R.layout.fragment_scanner) {

    @Inject
    lateinit var barcodeAnalyzer: BarcodeAnalyzer
    private var preview: Preview? = null
    private var camera: Camera? = null
    private lateinit var orientationEventListener: OrientationEventListener
    private lateinit var cameraExecutor: ExecutorService
    private var flashEnabled = false
    private val args: ScannerFragmentArgs by navArgs()
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    override fun initializeViews() {
        UiInterface.registerUi(
            showAppBar = false,
            showFab = true,
            fabIcon = R.drawable.ic_baseline_flash_off_24
        )
        cameraExecutor = Executors.newSingleThreadExecutor()
        binding.overlay.post {
            binding.overlay.setViewFinder()
        }
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {

        cameraProviderFuture.addListener({
            val rotation = binding.viewFinder.display.rotation

            preview = Preview.Builder()
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetRotation(rotation)
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { it.setAnalyzer(cameraExecutor, barcodeAnalyzer) }

            val cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            orientationEventListener = object : OrientationEventListener(requireContext()) {
                override fun onOrientationChanged(orientation: Int) {
                    when (orientation) {
                        in 45..134 -> Surface.ROTATION_270
                        in 135..224 -> Surface.ROTATION_180
                        in 225..314 -> Surface.ROTATION_90
                        else -> Surface.ROTATION_0
                    }.also { imageAnalysis.targetRotation = it }
                }
            }
            orientationEventListener.enable()

            barcodeAnalyzer.onResult {
                requireActivity().runOnUiThread {
                    imageAnalysis.clearAnalyzer()
                    cameraProvider?.unbindAll()
                    if (args.originDestination == 0) {
                        ScannerResultBottomSheet.newInstance(
                            it,
                            object : ScannerResultBottomSheet.DialogDismissListener {
                                override fun onDismiss() {
                                    bindPreview(cameraProvider)
                                }
                            })
                            .show(
                                parentFragmentManager,
                                ScannerResultBottomSheet::class.java.simpleName
                            )
                    } else {
                        findNavController().apply {
                            previousBackStackEntry!!.savedStateHandle.set(PRODUCT_BARCODE_KEY, it)
                            popBackStack()
                        }
                    }
                }
            }

            try {
                cameraProvider?.unbindAll()
                preview!!.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                camera = cameraProvider?.bindToLifecycle(
                    this, cameraSelector, imageAnalysis, preview
                )
                if (camera!!.cameraInfo.hasFlashUnit()) {
                    UiInterface
                        .setFabImageAndClickListener(R.drawable.ic_baseline_flash_off_24) {
                            camera?.cameraControl?.enableTorch(!flashEnabled)
                        }

                    camera!!.cameraInfo.torchState.observe(viewLifecycleOwner) {
                        it?.let { torchState ->
                            if (torchState == TorchState.ON) {
                                flashEnabled = true
                                UiInterface.setFabImage(R.drawable.ic_baseline_flash_on_24)
                            } else {
                                flashEnabled = false
                                UiInterface.setFabImage(R.drawable.ic_baseline_flash_on_24)
                            }
                        }
                    }
                }

            } catch (exc: Exception) {
                //error
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onDestroyView() {
        orientationEventListener.disable()
        camera = null
        preview = null
        super.onDestroyView()
    }

}