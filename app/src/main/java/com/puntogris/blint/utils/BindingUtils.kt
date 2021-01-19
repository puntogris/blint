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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.auth.FirebaseUser
import com.nguyenhoanglam.imagepicker.model.Image
import com.puntogris.blint.R
import com.puntogris.blint.model.MenuCard
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.custom_views.line_indicator.RallyVerticalBar
import com.puntogris.blint.ui.custom_views.line_indicator.RallyVerticalBarData
import com.puntogris.blint.utils.Constants.ACCOUNTING_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_CLIENTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_PRODUCTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_SUPPLIERS_CARD_CODE
import com.puntogris.blint.utils.Constants.CHARTS_CARD_CODE
import com.puntogris.blint.utils.Constants.RECORDS_CARD_CODE
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

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
    when(menuCard.code){
        ALL_PRODUCTS_CARD_CODE -> R.color.card1
        ALL_CLIENTS_CARD_CODE -> R.color.card2
        ALL_SUPPLIERS_CARD_CODE -> R.color.card3
        RECORDS_CARD_CODE -> R.color.card4
        CHARTS_CARD_CODE -> R.color.card5
        else -> R.color.card6
    }.also {
        setCardBackgroundColor(context.getColor(it))
    }
}

@BindingAdapter("menuCardIcon")
fun ImageView.setMenuCardIcon(menuCard: MenuCard){
    when(menuCard.code){
        ALL_PRODUCTS_CARD_CODE -> R.drawable.ic_baseline_library_books_24
        ALL_CLIENTS_CARD_CODE -> R.drawable.ic_baseline_people_alt_24
        ALL_SUPPLIERS_CARD_CODE -> R.drawable.ic_baseline_store_24
        ACCOUNTING_CARD_CODE -> R.drawable.ic_baseline_calendar_today_24
        RECORDS_CARD_CODE -> R.drawable.ic_baseline_article_24
        CHARTS_CARD_CODE -> R.drawable.ic_baseline_bar_chart_24
        else -> R.drawable.ic_baseline_add_24
    }.also {
        setImageDrawable(ContextCompat.getDrawable(context, it))
    }
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

@BindingAdapter("userDataImage")
fun ImageView.setUserDataImage(image: String?){
    if (!image.isNullOrBlank()){
        Glide.with(context)
            .load(image)
            .transform(RoundedCorners(20))
            .into(this)
    }else{
        Glide.with(context)
            .load(R.drawable.ic_baseline_account_circle_24)
            .transform(RoundedCorners(20))
            .into(this)
    }
}

@BindingAdapter("userCreationTimestamp")
fun TextView.setDateFromFirebaseUser(user:FirebaseUser?){
    if (user != null){
        user.metadata?.creationTimestamp?.let {
            text =  Date(it).getFormattedString()
        }
    }
}

@BindingAdapter("userRoleFormatted")
fun TextView.setUserRoleFormatted(role:String){
    text = role.toLowerCase(Locale.getDefault()).capitalize(Locale.getDefault())
}

@BindingAdapter("upperCaseToLowerCapitalize")
fun TextView.setUpperCaseToLowerCapitalize(role:String){
    text = role.toLowerCase(Locale.getDefault()).capitalize(Locale.getDefault())
}
