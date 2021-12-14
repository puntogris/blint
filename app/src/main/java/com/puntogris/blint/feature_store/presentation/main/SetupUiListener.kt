package com.puntogris.blint.feature_store.presentation.main

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.puntogris.blint.R

interface SetupUiListener {
    fun registerUi(
        showAppBar: Boolean = true,
        showToolbar: Boolean = true,
        showFabCenter: Boolean = true,
        @DrawableRes fabIcon: Int = R.drawable.ic_baseline_add_24,
        fabListener: View.OnClickListener? = null
    )

    fun setBottomAppBarInvisible()

    fun showSnackBar(
        message: String,
        duration: Int = Snackbar.LENGTH_SHORT,
        @StringRes actionText: Int = R.string.read_more,
        action: View.OnClickListener? = null
    )
}