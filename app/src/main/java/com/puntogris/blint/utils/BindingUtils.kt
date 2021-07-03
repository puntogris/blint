package com.puntogris.blint.utils

import android.os.Build
import android.view.View
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.chip.Chip
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.R
import com.puntogris.blint.model.Category
import com.puntogris.blint.model.Notification
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.custom_views.line_indicator.RallyVerticalBar
import com.puntogris.blint.ui.custom_views.line_indicator.RallyVerticalBarData
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

@BindingAdapter("menuCardIcon")
fun ImageView.setMenuCardIcon(@DrawableRes icon:Int){
    setImageDrawable(ContextCompat.getDrawable(context, icon))
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
    text = "${product.buyPrice.toUSDFormatted()} / ${product.sellPrice.toUSDFormatted()}"
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
            text =  Date(it).getDateFormattedString()
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

@BindingAdapter("clientOrSupplierTitleWithRecordType")
fun TextView.setClientOrSupplierTitleWithRecordType(type:String){
    text = if(type == "IN") "Proveedor" else "Cliente"
}

@BindingAdapter("dateFromTimestampWithTime")
fun TextView.setDateFromTimestampWithTime(timestamp: Timestamp){
    text =  timestamp.toDate().getDateWithTimeFormattedString()
}

@BindingAdapter("amountSymbolWithRecordType")
fun TextView.setAmountSymbolWithRecordType(type:String){
    text =  if(type == "IN") "+" else "-"
}

@BindingAdapter("recordTypeString")
fun TextView.setRecordTypeString(type:String){
    text =  if(type == "IN") "Entrada" else "Salida"
}


@BindingAdapter("dateFromTimestamp")
fun TextView.setDateFromTimestamp(timestamp: Timestamp?){
    timestamp?.let{
        text =  it.toDate().getDateFormattedString()
    }
}

@BindingAdapter("recordType")
fun View.setRecordType(type:String){
    if (type == "IN"){
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.card6, null))
    }else{
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.card1, null))
    }
}

@BindingAdapter("externalChipName")
fun Chip.setExternalChipName(name: String){
    if (name.isNotEmpty()){
        text = name
    }else{
        text = "No especificado"
        isEnabled = false
    }
}

@BindingAdapter("eventStatus")
fun TextView.setEventStatus(status: String){
    text = if (status == "PENDING") "Pendiente" else "Finalizado"
}

@BindingAdapter("eventStatusColor")
fun View.setEventStatusColor(status:String){
    if (status == "PENDING"){
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.card7, null))
    }else{
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.card6, null))
    }
}

@BindingAdapter("businessType")
fun TextView.setBusinessType(type:String){
    text = if(type == "LOCAL") "Local" else "Online"
}


@BindingAdapter("timerFromSeconds")
fun TextView.setTimerFromSeconds(seconds:Int){
    text = "${seconds / 60}m. ${seconds % 60}s."
}


@BindingAdapter("orderNumberTitle")
fun TextView.setOrderNumberTitle(order: Int){
    text = "Orden $order"
}

@BindingAdapter("debtColor")
fun TextView.setDebtColor(amount: Float){
    if (amount >= 0){
        text = "+$amount $"
        setTextColor(ContextCompat.getColor(context, R.color.card6))
    }else{
        text = "$amount $"
        setTextColor(ContextCompat.getColor(context, R.color.card1))
    }
}

@BindingAdapter("categoriesCheckTv")
fun CheckedTextView.setCategoriesCheckTv(category: Category){
    text = category.name
    val color = if (isDarkThemeOn()){
        if (category.selected) R.color.grey_60
        else R.color.almostBlack
    }else{
        if (category.selected) R.color.grey_20
        else R.color.grey_10
    }
    setBackgroundColor(context.getColor(color))
}

@BindingAdapter("notificationImage")
fun ImageView.setNotificationImage(uri:String){
    Glide.with(context)
        .load(uri)
        .transform(CenterCrop(),RoundedCorners(4))
        .into(this)
}

@BindingAdapter("timeSinceCreated")
fun TextView.setTimeSinceCreated(timestamp: Timestamp){
    val time = (Timestamp.now().seconds - timestamp.seconds) / 60
    text =
        when {
            time < 60 -> "$time mins."
            time in 61..1440 -> "${(time / 60).toInt()} hrs."
            else -> "${(time / 60 / 24).toInt()} dias"
        }
}

@BindingAdapter("notificationTypeTitle")
fun TextView.setNotificationTypeTitle(notification: Notification){
    text = when(notification.type){
        "NEW_USER" -> "Bienvenido a Blint!"
        "NEW_BUSINESS" -> "Felicitaciones!"
        "NEW_EMPLOYEE" -> "Novedad!"
        else -> {
            if (notification.title.isBlank()) gone()
            notification.title
        }
    }
}

@BindingAdapter("notificationTypeMessage")
fun TextView.setNotificationTypeMessage(notification: Notification){
    text = when(notification.type){
        "NEW_USER" -> "Te recomendamos que veas esta guia para poder aprovechar al maximo la app."
        "NEW_BUSINESS" -> "Hoy arranca una nueva aventura y estamos felices de poder acompañarte en ella."
        "NEW_EMPLOYEE" -> "El negocio se agranda, le damos la bienvenida a ${notification.message}."
        else -> {
            if (notification.message.isBlank()) gone()
            notification.message
        }
    }
}
