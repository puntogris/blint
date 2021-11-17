package com.puntogris.blint.utils

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.puntogris.blint.R
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.Constants.DISABLED
import com.puntogris.blint.utils.Constants.IN
import com.puntogris.blint.utils.Constants.INITIAL
import com.puntogris.blint.utils.Constants.LOCAL
import com.puntogris.blint.utils.Constants.PENDING
import com.puntogris.blint.utils.Constants.TO_DELETE
import java.util.*
import kotlin.math.absoluteValue

@BindingAdapter("imageFullSize")
fun ImageView.setImageFullSize(image: String) {
    if (image.isNotEmpty()) {
        GlideApp.with(context)
            .load(image)
            .transform(CenterCrop(), RoundedCorners(5))
            .transform()
            .into(this)
        visible()
    } else gone()
}

@BindingAdapter("menuCardIcon")
fun ImageView.setMenuCardIcon(@DrawableRes icon: Int) {
    setImageDrawable(ContextCompat.getDrawable(context, icon))
}

@BindingAdapter("loadImageButtonText")
fun Button.setLoadImageButtonText(image: String) {
    text =
        if (image.isNotEmpty()) context.getString(R.string.action_change)
        else context.getString(R.string.action_add_image)
}

@BindingAdapter("removeImageVisibility")
fun Button.setRemoveImageVisibility(image: String) {
    if (image.isNotEmpty()) visible() else gone()
}

@BindingAdapter("removeImageVisibility")
fun TextView.setRemoveImageVisibility(image: String) {
    if (image.isNotEmpty()) visible() else gone()
}

@BindingAdapter("removeListVisibility")
fun TextView.setRemoveListVisibility(list: List<Any>?) {
    if (list.isNullOrEmpty()) gone() else visible()
}

@BindingAdapter("removeListVisibility")
fun CardView.setRemoveListVisibility(product: ProductWithSuppliersCategories) {
    if (product.categories.isNullOrEmpty() && product.suppliers.isNullOrEmpty()) gone() else visible()
}

@BindingAdapter("emptyEditTextWithNumber")
fun EditText.setEmptyEditTextWithNumber(value: Number) {
    setText(if (value.toInt() == 0) "" else value.toString())
}

@BindingAdapter("productPrices")
fun TextView.setProductPrices(product: Product) {
    "${product.buyPrice.toUSDFormatted()} / ${product.sellPrice.toUSDFormatted()}".also {
        text = it
    }
}

@BindingAdapter("userDataImage")
fun ImageView.setUserDataImage(image: String?) {
    if (!image.isNullOrBlank()) {
        Glide.with(context)
            .load(image)
            .transform(RoundedCorners(20))
            .into(this)
    } else {
        Glide.with(context)
            .load(R.drawable.ic_baseline_account_circle_24)
            .transform(RoundedCorners(20))
            .into(this)
    }
}

@BindingAdapter("userCreationTimestamp")
fun TextView.setDateFromFirebaseUser(user: FirebaseUser?) {
    if (user != null) {
        user.metadata?.creationTimestamp?.let {
            text = Date(it).getDateFormattedString()
        }
    }
}

@BindingAdapter("userRoleFormatted")
fun TextView.setUserRoleFormatted(role: String) {
    text = role.lowercase(Locale.getDefault()).capitalizeFirstChar()
}

@BindingAdapter("capitalizeFirstChar")
fun TextView.setCapitalizeFirstChar(text: String) {
    this.text = text.capitalizeFirstChar()
}

@BindingAdapter("capitalizeWord")
fun TextView.setCapitalizeWord(text: String) {
    this.text = text.split(" ").joinToString(" ") { it.capitalizeFirstChar() }
}

@BindingAdapter("numberToMoneyString")
fun TextView.setNumberToMoneyString(number: Float) {
    text = context.getString(R.string.amount_normal, number.toMoneyFormatted())
}

@BindingAdapter("clientOrSupplierTitleWithRecordType")
fun TextView.setClientOrSupplierTitleWithRecordType(type: String) {
    text =
        if (type == IN) context.getString(R.string.supplier_label)
        else context.getString(R.string.client_label)
}


@BindingAdapter("totalOrderWithDetails")
fun TextView.setTotalOrderWithDetails(order: OrderWithRecords) {
    var data = ""

    if (order.order.discount != 0F) {
        data += context.getString(
            R.string.order_value_discount_summary,
            order.order.discount.toMoneyFormatted()
        )
    }

    if (order.debt != null) {
        val debt = order.debt?.amount?.absoluteValue?.toMoneyFormatted()
        if (data.isBlank()) {
            data = if (order.order.type == IN) context.getString(
                R.string.order_value_debt_supplier_summary,
                debt
            )
            else context.getString(R.string.order_value_debt_client_summary, debt)
        } else {
            data += if (order.order.type == IN) " " + context.getString(
                R.string.order_value_debt_supplier_summary2,
                debt
            )
            else " " + context.getString(R.string.order_value_debt_client_summary2, debt)
        }
    } else {
        if (order.order.discount != 0F) data += "."
    }

    if (data.isEmpty()) gone() else text = data
}

@BindingAdapter("dateFromTimestampWithTime")
fun TextView.setDateFromTimestampWithTime(timestamp: Timestamp) {
    text = timestamp.toDate().getDateWithTimeFormattedString()
}

@BindingAdapter("amountSymbolWithRecordType")
fun TextView.setAmountSymbolWithRecordType(type: String) {
    text = if (type == IN || type == INITIAL) "+" else "-"
}

@BindingAdapter("recordTypeString")
fun TextView.setRecordTypeString(type: String) {
    text =
        if (type == IN || type == INITIAL) context.getString(R.string.in_entry)
        else context.getString(R.string.out_entry)
}

@BindingAdapter("dateFromTimestamp")
fun TextView.setDateFromTimestamp(timestamp: Timestamp?) {
    timestamp?.let {
        text = it.toDate().getDateFormattedString()
    }
}

@BindingAdapter("deletionTimestamp")
fun TextView.setDeletionTimestamp(timestamp: Timestamp?) {
    timestamp?.let {
        val calendar = Calendar.getInstance()
        calendar.time = it.toDate()
        calendar.add(Calendar.DAY_OF_YEAR, 15)
        text = calendar.time.getDateFormattedString()
    }
}

@BindingAdapter("recordType")
fun View.setRecordType(type: String) {
    val color = if (type == IN || type == INITIAL) R.color.card6 else R.color.card1
    setBackgroundColor(ResourcesCompat.getColor(resources, color, null))
}

@BindingAdapter("externalChipName")
fun Chip.setExternalChipName(name: String) {
    if (name.isNotEmpty()) {
        text = name.capitalizeFirstChar()
    } else {
        text = context.getString(R.string.not_specified)
        isEnabled = false
    }
}

@BindingAdapter("externalName")
fun TextView.setExternalName(name: String) {
    text = if (name.isNotEmpty()) name.capitalizeFirstChar()
    else context.getString(R.string.not_specified)
}

@BindingAdapter("eventStatus")
fun TextView.setEventStatus(status: String) {
    text =
        if (status == PENDING) context.getString(R.string.pending)
        else context.getString(R.string.finished)
}

@BindingAdapter("eventStatusColor")
fun View.setEventStatusColor(status: String) {
    if (status == PENDING) {
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.card7, null))
    } else {
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.card6, null))
    }
}

@BindingAdapter("businessType")
fun TextView.setBusinessType(type: String) {
    text =
        if (type == LOCAL) context.getString(R.string.local) else context.getString(R.string.online)
}

@BindingAdapter("timerFromSeconds")
fun TextView.setTimerFromSeconds(seconds: Int) {
    text = context.getString(R.string.timer_with_params, seconds / 60, seconds % 60)
}

@BindingAdapter("orderNumberTitle")
fun TextView.setOrderNumberTitle(number: Int) {
    text = context.getString(R.string.order_number, number)
}

@BindingAdapter("debtColor")
fun TextView.setDebtColor(amount: Float) {
    if (amount >= 0) {
        text = context.getString(R.string.amount_debt_positive, amount.toMoneyFormatted())
        setTextColor(ContextCompat.getColor(context, R.color.card6))
    } else {
        text = context.getString(R.string.amount_normal, amount.toMoneyFormatted())
        setTextColor(ContextCompat.getColor(context, R.color.card1))
    }
}

@BindingAdapter("debtColorWithLimit")
fun TextView.setDebtColorWithLimit(amount: Float) {
    val newAmount =
        if (amount > 1000000) (amount / 1000000).toMoneyFormatted() + "M." else amount.toMoneyFormatted()
    if (amount >= 0) {
        text = context.getString(R.string.amount_debt_positive, newAmount)
        setTextColor(ContextCompat.getColor(context, R.color.card6))
    } else {
        text = context.getString(R.string.amount_normal, newAmount)
        setTextColor(ContextCompat.getColor(context, R.color.card1))
    }
}


@BindingAdapter("notificationImage")
fun ImageView.setNotificationImage(uri: String) {
    Glide.with(context)
        .load(uri)
        .transform(CenterCrop(), RoundedCorners(4))
        .into(this)
}

@BindingAdapter("timeSinceCreated")
fun TextView.setTimeSinceCreated(timestamp: Timestamp) {
    val minutes = ((Timestamp.now().seconds - timestamp.seconds) / 60).toInt()
    text =
        when {
            minutes in 0..1 -> context.getString(R.string.minute_notif, minutes)
            minutes < 60 -> context.getString(R.string.minutes_notif, minutes)
            minutes == 60 -> context.getString(R.string.hour_notif, (minutes / 60))
            minutes in 61..1440 -> context.getString(R.string.hours_notif, (minutes / 60))
            minutes in 1440..2880 -> context.getString(R.string.day_notif)
            else -> context.getString(R.string.days_notif, (minutes / 60 / 24))
        }
}

@BindingAdapter("productCategoriesChipGroup")
fun ChipGroup.setProductCategoriesChip(categories: List<Category>?) {
    val data = this.children.map { (it as Chip).text }
    categories?.forEach {
        if (!data.contains(it.categoryName)) {
            val chip = Chip(context)
            chip.text = it.categoryName.capitalizeFirstChar()
            addView(chip)
        }
    }
}

@BindingAdapter("productSuppliersChipGroup")
fun ChipGroup.setProductSuppliersChips(suppliers: List<FirestoreSupplier>?) {
    val data = this.children.map { (it as Chip).text }
    suppliers?.forEach {
        if (!data.contains(it.companyName)) {
            val chip = Chip(context)
            chip.text = it.companyName.capitalizeFirstChar()
            addView(chip)
        }
    }
}

@BindingAdapter("businessStatus")
fun TextView.setBusinessStatus(status: String) {
    text = when (status) {
        TO_DELETE -> context.getString(R.string.on_delete)
        DISABLED -> context.getString(R.string.disabled)
        else -> context.getString(R.string.enabled)
    }
}

@BindingAdapter("productOrderPrices")
fun TextView.setProductOrderPrices(product: ProductWithRecord) {
    text = if (product.record.type == IN) {
        product.product.buyPrice.toMoneyFormatted()
    } else {
        "${product.product.sellPrice.toMoneyFormatted()} /${product.product.suggestedSellPrice.toMoneyFormatted()}"
    }
}

@BindingAdapter("productOrderPricesTitle")
fun TextView.setProductOrderPricesTitle(record: Record) {
    text = context.getString(if (record.type == IN) R.string.buy_price else R.string.sell_prices)

}

@BindingAdapter("productRecordPriceEntry")
fun TextView.setProductRecordPriceEntry(product: ProductWithRecord) {
    text = if (product.record.type == IN) {
        product.product.buyPrice.toString()
    } else {
        product.product.sellPrice.toString()
    }
}