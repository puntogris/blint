package com.puntogris.blint.utils

import android.graphics.Color
import android.os.Build
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.nguyenhoanglam.imagepicker.model.Image
import com.puntogris.blint.R
import com.puntogris.blint.model.MenuCard
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.custom_views.line_indicator.RallyVerticalBar
import com.puntogris.blint.ui.custom_views.line_indicator.RallyVerticalBarData

@BindingAdapter("imageFullSize")
fun ImageView.setImageFullSize(image: HashMap<String,String>?){
    if(image != null && image.containsImage()){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            GlideApp.with(context)
                    .load(image["uri"])
                    .transform(CenterCrop(), RoundedCorners(5))
                    .into(this)
        } else {
            GlideApp.with(context)
                    .load(image["path"])
                    .transform(CenterCrop(), RoundedCorners(5))
                    .transform()
                    .into(this)
        }
        visible()
    }else gone()
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
fun Button.setLoadImageButtonText(image: HashMap<String, String>){
    text = if (image.containsImage()) {
        "Cambiar"
    } else {
        "Agregar imagen"
    }
}

@BindingAdapter("removeImageVisibility")
fun Button.setRemoveImageVisibility(image: HashMap<String, String>){
    if (image.containsImage()) visible() else gone()

}

@BindingAdapter("verticalIndicatorProgress")
fun RallyVerticalBar.setVerticalIndicatorProgress(amount: Float){
    val newAmount = if (amount >= 100F) 100F else amount
    this.renderData(RallyVerticalBarData(newAmount, 100F, R.color.teal_200))
}

@BindingAdapter("emptyEditTextWithNumber")
fun EditText.setEmptyEditTextWithNumber(value: Number){
    setText(if (value.toInt() == 0) "" else value.toString())
}

@BindingAdapter("productPrices")
fun TextView.setProductPrices(product: Product){
    text = "${product.buyPrice.toFloat().toUSDFormatted()} / ${product.sellPrice.toFloat().toUSDFormatted()}"
}