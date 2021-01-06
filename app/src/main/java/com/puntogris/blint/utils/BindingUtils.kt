package com.puntogris.blint.utils

import android.graphics.Color
import android.os.Build
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.nguyenhoanglam.imagepicker.model.Image
import com.puntogris.blint.R
import com.puntogris.blint.model.MenuCard
import com.puntogris.blint.ui.custom_views.line_indicator.RallyVerticalBar
import com.puntogris.blint.ui.custom_views.line_indicator.RallyVerticalBarData

@BindingAdapter("imageFullSize")
fun ImageView.setImageFullSize(image: Image?){
    if(image != null){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            GlideApp.with(context)
                    .load(image.uri)
                    .transform(CenterCrop(), RoundedCorners(5))
                    .into(this)
        } else {
            GlideApp.with(context)
                    .load(image.path)
                    .transform(CenterCrop(), RoundedCorners(5))
                    .transform()
                    .into(this)
        }
    }
}

@BindingAdapter("menuCardColor")
fun CardView.setBackgroundColor(menuCard: MenuCard){
    setCardBackgroundColor(context.getColor(menuCard.color))
}

@BindingAdapter("menuCardIcon")
fun ImageView.setMenuCardIcon(menuCard: MenuCard){
    setImageDrawable(ContextCompat.getDrawable(context, menuCard.icon))
    setColorFilter(Color.WHITE)
}

@BindingAdapter("loadImageButtonText")
fun Button.setLoadImageButtonText(image: Image?){
    text = if (image == null) "Agregar imagen" else "Cambiar imagen"
}

@BindingAdapter("verticalIndicatorProgress")
fun RallyVerticalBar.setVerticalIndicatorProgress(amount: Int){
    val newAmount = if (amount >= 100) 100 else amount
    this.renderData(RallyVerticalBarData(newAmount.toFloat(), 100F,R.color.teal_200))
}

@BindingAdapter("emptyEditTextWithInt")
fun EditText.setEmptyEditTextWithInt(value: Int){
    setText(if (value == 0) "" else value.toString())
}