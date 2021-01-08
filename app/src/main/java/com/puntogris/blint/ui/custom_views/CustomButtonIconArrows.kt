package com.puntogris.blint.ui.custom_views

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import com.puntogris.blint.R

class CustomButtonIconArrows(context: Context, attributeSet: AttributeSet): MaterialButton(context, attributeSet) {

    private var clicked = false

    fun toggleIcon(){
        icon = if (!clicked){
            ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_keyboard_arrow_up_24, null)
        }else{
            ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_keyboard_arrow_down_24, null)
        }
        clicked = !clicked
    }
}