package com.puntogris.blint.common.utils

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Constants.IN
import com.puntogris.blint.common.utils.Constants.INITIAL
import com.puntogris.blint.common.utils.Constants.PENDING
import com.puntogris.blint.feature_store.domain.model.Category
import com.puntogris.blint.feature_store.domain.model.Trader
import com.puntogris.blint.feature_store.domain.model.order.NewOrder
import com.puntogris.blint.feature_store.domain.model.order.OrderWithRecords
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails
import org.threeten.bp.OffsetDateTime
import java.util.*

@BindingAdapter("imageFullSize")
fun ImageView.setImageFullSize(image: String) {
    if (image.isNotEmpty()) {
        GlideApp.with(context)
            .load(image)
            .transform()
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade(Constants.CROSS_FADE_DURATION))
            .into(this)
        visible()
    } else gone()
}

@BindingAdapter("profileImage")
fun ImageView.setProfileImage(image: String?) {
    if (!image.isNullOrEmpty()) {
        GlideApp.with(context)
            .load(image)
            .circleCrop()
            .into(this)
    }
}

@BindingAdapter("loadImageButtonText")
fun Button.setLoadImageButtonText(image: String) {
    setText(
        if (image.isNotEmpty()) R.string.action_change else R.string.action_add_image
    )
}

@BindingAdapter("removeImageVisibility")
fun Button.setRemoveImageVisibility(image: String) {
    isVisible = image.isNotEmpty()
}

@BindingAdapter("removeImageVisibility")
fun TextView.setRemoveImageVisibility(image: String) {
    isVisible = image.isNotEmpty()
}

@BindingAdapter("removeListVisibility")
fun TextView.setRemoveListVisibility(list: List<Any>?) {
    isVisible = !list.isNullOrEmpty()
}

@BindingAdapter("removeListVisibility")
fun TextView.setRemoveListVisibility(product: ProductWithDetails) {
    if (product.categories.isNullOrEmpty() && product.traders.isNullOrEmpty()) gone() else visible()
}

@BindingAdapter("emptyEditTextWithNumber")
fun EditText.setEmptyEditTextWithNumber(value: Number) {
    setText(if (value.toInt() == 0) "0" else value.toString())
}

@BindingAdapter("productPrices")
fun TextView.setProductPrices(product: Product) {
    text = "${product.buyPrice.toUSDFormatted()} / ${product.sellPrice.toUSDFormatted()}"
}

@BindingAdapter("userDataImage")
fun ImageView.setUserDataImage(image: String?) {
    Glide.with(context)
        .load(image ?: R.drawable.ic_baseline_account_circle_24)
        .circleCrop()
        .into(this)
}

@BindingAdapter("userCreationTimestamp")
fun TextView.setDateFromFirebaseUser(date: OffsetDateTime?) {
    if (date != null) {
        text = date.getDateWithTimeFormattedString()
    }
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
    //todo, maybe use type and show here
    setText(if (type == IN) R.string.trader_label else R.string.trader_label)
}

@BindingAdapter("totalOrderWithDetails")
fun TextView.setTotalOrderWithDetails(order: OrderWithRecords) {
    val total = order.order.value.toString()
    text = if (order.debt != null) {
        context.getString(R.string.order_with_debt, total, order.debt?.amount.toString())
    } else {
        context.getString(R.string.order_fully_paid, total)
    }
}

@BindingAdapter("dateFromTimestampWithTime")
fun TextView.setDateFromTimestampWithTime(timestamp: OffsetDateTime) {
    text = timestamp.getDateWithTimeFormattedString()
}

@BindingAdapter("amountSymbolWithRecordType")
fun TextView.setAmountSymbolWithRecordType(type: String) {
    text = if (type == IN || type == INITIAL) "+" else "-"
}

@BindingAdapter("recordTypeString")
fun TextView.setRecordTypeString(type: String) {
    setText(
        if (type == IN || type == INITIAL) R.string.in_entry
        else R.string.out_entry
    )
}

@BindingAdapter("dateFromTimestamp")
fun TextView.setDateFromTimestamp(timestamp: OffsetDateTime?) {
    timestamp?.let {
        text = it.getDateFormattedString()
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
        setText(R.string.not_specified)
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
    setText(if (status == PENDING) R.string.pending else R.string.finished)
}

@BindingAdapter("eventStatusColor")
fun View.setEventStatusColor(status: String) {
    val color = if (status == PENDING) R.color.card7 else R.color.card6
    setBackgroundColor(ResourcesCompat.getColor(resources, color, null))
}

@BindingAdapter("orderNumberTitle")
fun TextView.setOrderNumberTitle(number: Int) {
    text = context.getString(R.string.order_number, number)
}

@BindingAdapter("debtColor")
fun TextView.setDebtColor(amount: Float) {
    val (res, color) = if (amount >= 0) {
        R.string.amount_debt_positive to R.color.card6
    } else {
        R.string.amount_normal to R.color.card1
    }
    text = context.getString(res, amount.toMoneyFormatted())
    setTextColor(ContextCompat.getColor(context, color))
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

@BindingAdapter("productCategoriesChipGroup")
fun ChipGroup.setProductCategoriesChip(categories: List<Category>) {
    categories.forEach {
        val chip = Chip(context).apply {
            text = it.categoryName.capitalizeFirstChar()
        }
        addView(chip)
    }
}

@BindingAdapter("productSuppliersChipGroup")
fun ChipGroup.setProductSuppliersChips(suppliers: List<Trader>) {
    suppliers.forEach {
        val chip = Chip(context).apply {
            text = it.name.capitalizeFirstChar()
        }
        addView(chip)
    }
}

fun TextView.setDateOrError(timeInMillis: Long) {
    text = if (timeInMillis == 0L) {
        context.getString(R.string.no_backup_found)
    } else {
        Date(timeInMillis).getDateWithTimeFormattedString()
    }
}

@BindingAdapter("orderTotal")
fun TextView.setOrderTotal(total: Float) {
    text = context.getString(
        R.string.order_total_template,
        total.toString()
    )
}

@BindingAdapter("pricesAndStockTitle")
fun TextView.setPricesAndStockTitle(productId: String) {
    setText(
        if (productId.isEmpty()) R.string.prices_and_initial_stock else R.string.prices_and_stock
    )
}

@BindingAdapter("orderDebtSummary")
fun TextView.setOrderDebtSummary(newOrder: NewOrder) {
    val totalPaid = if (newOrder.newDebt != null) {
        newOrder.value - (newOrder.newDebt?.amount ?: 0F)
    } else {
        newOrder.value
    }
    text = context.getString(
        R.string.order_debt_amount_summary,
        totalPaid.toString(),
        newOrder.value.toString()
    )
}

@BindingAdapter("traderTypeToggleGroup")
fun MaterialButtonToggleGroup.setTraderTypeToggleGroup(type: String) {
//    val button = when (type) {
//        Constants.SUPPLIER -> R.id.traderTypeSupplierButton
//        Constants.CLIENT -> R.id.traderTypeClientButton
//        else -> R.id.traderTypeOtherButton
//    }
  //  if (checkedButtonId != button) check(button)
}

@BindingAdapter("traderType")
fun TextView.setTraderType(type: String) {
    val res = when (type) {
        Constants.SUPPLIER -> R.string.trader_type_supplier
        Constants.CLIENT -> R.string.trader_type_client
        else -> R.string.trader_type_other
    }
    setText(res)
}