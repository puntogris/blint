package com.puntogris.blint.ui.main

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.puntogris.blint.R

interface SetupUiListener{
    fun register(showFab: Boolean = false,
                 showAppBar: Boolean = true,
                 showToolbar: Boolean = true,
                 showFabCenter: Boolean = true,
                 @DrawableRes fabIcon: Int = R.drawable.ic_baseline_add_24,
                 fabListener: View.OnClickListener? = null)

    fun updateBadge(value: Int)
    fun setBottomAppBarInvisible()
    fun hideFab()
    fun setFabImageAndClickListener(
        @DrawableRes fabIcon: Int = R.drawable.ic_baseline_add_24,
        fabListener: View.OnClickListener? = null
    )
    fun setFabImage(@DrawableRes fabIcon: Int)
    fun setToolbarAndStatusBarColor(@ColorRes color:Int)
    fun setDarkStatusBar()
    fun showSnackBar(message: String,
                     duration: Int = Snackbar.LENGTH_SHORT,
                     @StringRes actionText: Int = R.string.read_more,
                     action: View.OnClickListener? = null)

}