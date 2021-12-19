package com.puntogris.blint.feature_store.presentation.scanner

import android.util.Size
import android.view.OrientationEventListener
import android.view.Surface
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.toggleFlash
import com.puntogris.blint.databinding.FragmentScannerBinding
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
    private lateinit var cameraProvider: ProcessCameraProvider
    private val args: ScannerFragmentArgs by navArgs()

    override fun initializeViews() {
        binding.fragment = this

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.overlay.post {
            binding.overlay.setViewFinder()
        }

        startCamera()

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
        val rotation = binding.viewFinder.display.rotation

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

        orientationEventListener = getOrientationEventListener(imageAnalysis)
            .also { it.enable() }

        barcodeAnalyzer.onResult {
            requireActivity().runOnUiThread {
                imageAnalysis.clearAnalyzer()
                cameraProvider.unbindAll()

                //todo create constants for this int, to get context of what is this for
                if (args.originDestination == 0) {
                    val action =
                        ScannerFragmentDirections.actionScannerFragmentToScannerResultDialog(it)
                    findNavController().navigate(action)
                } else {
                    setFragmentResult(
                        Keys.SCANNER_RESULT_KEY,
                        bundleOf(Keys.PRODUCT_BARCODE_KEY to it)
                    )
                    findNavController().navigateUp()
                }
            }
        }

        try {
            cameraProvider.unbindAll()

            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, imageAnalysis, preview
            )

            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)

            observerCameraState(camera?.cameraInfo!!)

        } catch (e: Exception) {
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
                binding.flashButton.setImageResource(flashIcon)
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

    fun onToggleFlashClicked() {
        camera?.toggleFlash()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        orientationEventListener.disable()
        camera = null
        preview = null
    }
}
