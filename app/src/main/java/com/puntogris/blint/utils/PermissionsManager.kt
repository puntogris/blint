package com.puntogris.blint.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.puntogris.blint.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PermissionsManager @Inject constructor(@ApplicationContext private val context: Context,
) {

    fun hasCameraPermission() = ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED

    fun requestCameraPermissionAndNavigateToScanner(fragment: Fragment){
        val requestPermissionLauncher =
            fragment.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) fragment.findNavController().navigate(R.id.scannerFragment)
                else fragment.showLongSnackBarAboveFab("Necesitamos acceso a la camara para poder abrir el escaner.")
            }

        if (hasCameraPermission()) fragment.findNavController().navigate(R.id.scannerFragment)
        else requestPermissionLauncher.launch(Manifest.permission.CAMERA)

    }


}