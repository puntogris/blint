package com.puntogris.blint.utils

import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
import com.puntogris.blint.model.MenuCard
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.notifications.EmploymentRequestReceivedNotif
import com.puntogris.blint.model.notifications.EmploymentRequestSentNotif
import com.puntogris.blint.ui.custom_views.line_indicator.RallyVerticalBar
import com.puntogris.blint.ui.custom_views.line_indicator.RallyVerticalBarData
import com.puntogris.blint.utils.Constants.ACCOUNTING_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_CLIENTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_PRODUCTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_SUPPLIERS_CARD_CODE
import com.puntogris.blint.utils.Constants.CHARTS_CARD_CODE
import com.puntogris.blint.utils.Constants.OPERATIONS_CARD_CODE
import com.puntogris.blint.utils.Constants.RECORDS_CARD_CODE
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
fun ImageView.setMenuCardIcon(menuCard: MenuCard){
    when(menuCard.code){
        ALL_PRODUCTS_CARD_CODE -> R.drawable.ic_packages
        ALL_CLIENTS_CARD_CODE -> R.drawable.ic_rating
        ALL_SUPPLIERS_CARD_CODE -> R.drawable.ic_shipped
        ACCOUNTING_CARD_CODE -> R.drawable.ic_calendar
        RECORDS_CARD_CODE -> R.drawable.ic_report
        CHARTS_CARD_CODE -> R.drawable.ic_analytics
        OPERATIONS_CARD_CODE -> R.drawable.ic_baseline_grain_24
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

@BindingAdapter("employmentRequestSentMessage")
fun TextView.setEmploymentRequestSentMessage(request: EmploymentRequestSentNotif){
    text = "Se envio una solicitud de empleo para el negocio ${request.businessName} al email ${request.employeeEmail}."
}

@BindingAdapter("employmentRequestReceivedMessage")
fun TextView.setEmploymentRequestReceivedMessage(request: EmploymentRequestReceivedNotif){
    text = "Recibiste una solicitud de empleo para el negocio ${request.businessName}."
}

@BindingAdapter("timerFromSeconds")
fun TextView.setTimerFromSeconds(seconds:Int){
    text = "${seconds / 60}m. ${seconds % 60}s."
}