package com.puntogris.blint.feature_store.presentation.scanner

import android.os.Bundle
import android.util.Size
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.toggleFlash
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentScannerBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class ScannerFragment : Fragment(R.layout.fragment_scanner) {

    @Inject
    lateinit var barcodeAnalyzer: BarcodeAnalyzer
    private var preview: Preview? = null
    private var camera: Camera? = null
    private lateinit var orientationEventListener: OrientationEventListener
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private val args: ScannerFragmentArgs by navArgs()

    private val binding by viewBinding(FragmentScannerBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbarBackButton(binding.toolbar)

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.overlayScanner.post {
            binding.overlayScanner.setViewFinder()
        }

        startCamera()
        setupListeners()
    }

    private fun setupListeners() {
        binding.buttonToggleFlash.setOnClickListener {
            camera?.toggleFlash()
        }
        setFragmentResultListener(Keys.SCANNER_FRAGMENT_KEY) { _, bundle ->
            if (bundle.getBoolean(Keys.RESUME_CAMERA_KEY)) bindCameraUseCases()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            bindCameraUseCases()

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraUseCases() {
        val rotation = binding.previewViewScanner.display.rotation

        preview = Preview.Builder()
            .setTargetRotation(rotation)
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

        orientationEventListener = getOrientationEventListener(imageAnalysis).also { it.enable() }

        barcodeAnalyzer.onResult {
            requireActivity().runOnUiThread {
                imageAnalysis.clearAnalyzer()
                cameraProvider.unbindAll()

                if (args.returnResult) {
                    setFragmentResult(
                        Keys.SCANNER_RESULT_KEY,
                        bundleOf(Keys.PRODUCT_BARCODE_KEY to it)
                    )
                    findNavController().navigateUp()
                } else {
                    val action =
                        ScannerFragmentDirections.actionScannerFragmentToScannerResultDialog(it)
                    findNavController().navigate(action)
                }
            }
        }

        try {
            cameraProvider.unbindAll()

            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                imageAnalysis,
                preview
            )

            preview?.setSurfaceProvider(binding.previewViewScanner.surfaceProvider)
            val cameraInfo = requireNotNull(camera?.cameraInfo)
            observerCameraState(cameraInfo)
        } catch (ignored: Exception) {
            UiInterface.showSnackBar(getString(R.string.snack_an_error_occurred))
        }
    }

    private fun observerCameraState(cameraInfo: CameraInfo) {
        if (cameraInfo.hasFlashUnit()) {
            cameraInfo.torchState.observe(viewLifecycleOwner) { torchState ->
                val flashIcon = when (torchState) {
                    TorchState.ON -> R.drawable.ic_baseline_flash_off_24
                    else -> R.drawable.ic_baseline_flash_on_24
                }
                binding.buttonToggleFlash.setImageResource(flashIcon)
            }
        }
    }

    private fun getOrientationEventListener(imageAnalysis: ImageAnalysis): OrientationEventListener {
        return object : OrientationEventListener(requireContext()) {
            override fun onOrientationChanged(orientation: Int) {
                when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }.also { imageAnalysis.targetRotation = it }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        orientationEventListener.disable()
        camera = null
        preview = null
    }
}
