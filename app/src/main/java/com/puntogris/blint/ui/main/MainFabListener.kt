package com.puntogris.blint.ui.main

import android.view.View
import androidx.annotation.DrawableRes
import com.puntogris.blint.R

interface MainFabListener{
    fun addListener(showFab: Boolean = false,
                    showAppBar: Boolean = true,
                    showToolbar: Boolean = true,
                    showFabCenter: Boolean = true,
                    @DrawableRes fabIcon: Int = R.drawable.ic_baseline_add_24,
                    fabListener: View.OnClickListener? = null)
}