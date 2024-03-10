package com.puntogris.blint.common.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.db.williamchart.view.DonutChartView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Constants.IN
import com.puntogris.blint.common.utils.Constants.INITIAL
import com.puntogris.blint.common.utils.Constants.PENDING
import com.puntogris.blint.feature_store.domain.model.Category
import com.puntogris.blint.feature_store.domain.model.Trader
import com.puntogris.blint.feature_store.domain.model.Traffic
import com.puntogris.blint.feature_store.domain.model.order.NewOrder
import com.puntogris.blint.feature_store.domain.model.order.NewRecord
import com.puntogris.blint.feature_store.domain.model.order.OrderWithRecords
import com.puntogris.blint.feature_store.domain.model.product.Product
import org.threeten.bp.OffsetDateTime
import java.util.Date
import kotlin.math.absoluteValue

/*
 * For some reason if we set the drawable inside glide it uses the theme of the phone, ignoring the
 * one in the settings, displaying the wrong background color.
 */
fun ImageView.setImageFullSize(image: String) {
    if (image.isNotEmpty()) {
        GlideApp.with(context)
            .load(image)
            .transform()
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade(Constants.CROSS_FADE_DURATION))
            .into(this)
    } else {
        Glide.with(context)
            .clear(this)
        setBackgroundResource(R.color.color_on_primary)
    }
}

fun ImageView.setProfileImage(image: String?) {
    if (!image.isNullOrEmpty()) {
        GlideApp.with(context)
            .load(image)
            .circleCrop()
            .into(this)
    }
}

fun TextView.setProductPrices(product: Product) {
    text = context.getString(
        R.string.product_buy_sell_prices_template,
        product.buyPrice.toMoneyFormatted(),
        product.sellPrice.toMoneyFormatted()
    )
}

fun ImageView.setUserDataImage(image: String?) {
    Glide.with(context)
        .load(image ?: R.drawable.ic_baseline_account_circle_24)
        .circleCrop()
        .into(this)
}

fun TextView.setDateFromFirebaseUser(date: OffsetDateTime?) {
    if (date != null) {
        text = date.getDateWithTimeFormattedString()
    }
}

fun TextView.setCapitalizeFirstChar(text: String?) {
    this.text = text?.capitalizeFirstChar()
}

fun TextView.setCapitalizeWord(text: String) {
    this.text = text.split(" ").joinToString(" ") { it.capitalizeFirstChar() }
}

fun TextView.setNumberToMoneyString(number: Float) {
    text = context.getString(R.string.amount_normal, number.toMoneyFormatted())
}

fun TextView.setTotalOrderWithDetails(order: OrderWithRecords) {
    val total = order.order.total.toString()
    text = if (order.debt != null) {
        context.getString(R.string.order_with_debt, total, order.debt?.amount.toString())
    } else {
        context.getString(R.string.order_fully_paid, total)
    }
}

fun TextView.setDateFromTimestampWithTime(timestamp: OffsetDateTime) {
    text = timestamp.getDateWithTimeFormattedString()
}

fun TextView.setAmountSymbolWithRecordType(type: String) {
    text = if (type == IN || type == INITIAL) "+" else "-"
}

fun TextView.setRecordTypeString(type: String) {
    setText(
        if (type == IN || type == INITIAL) R.string.in_entry
        else R.string.out_entry
    )
}

fun TextView.setDateFromTimestamp(timestamp: OffsetDateTime?) {
    timestamp?.let {
        text = it.getDateFormattedString()
    }
}

fun View.setRecordType(type: String) {
    val color = if (type == IN || type == INITIAL) R.color.card6 else R.color.card1
    backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, color, null))
}

fun Chip.setExternalChipName(name: String) {
    if (name.isNotEmpty()) {
        text = name.capitalizeFirstChar()
    } else {
        setText(R.string.not_specified)
        isEnabled = false
    }
}

fun TextView.setExternalName(name: String) {
    text = if (name.isNotEmpty()) name.capitalizeFirstChar()
    else context.getString(R.string.enter_trader_hint)
}


fun TextView.setOrderDebtOrDefault(newOrder: NewOrder) {
    text = newOrder.newDebt?.amount?.let {
        context.getString(R.string.debt_of_template, it.toMoneyFormatted())
    } ?: context.getString(R.string.enter_debt_hint)
}

fun TextView.setEventStatus(status: String) {
    setText(if (status == PENDING) R.string.pending else R.string.finished)
}

fun View.setEventStatusColor(status: String) {
    val color = if (status == PENDING) R.color.card7 else R.color.card6
    backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(resources, color, null))
}

fun TextView.setOrderNumberTitle(number: Int) {
    text = context.getString(R.string.order_number, number)
}

fun TextView.setDebtColor(amount: Float) {
    val (res, color) = if (amount >= 0) {
        R.string.amount_debt_positive to R.color.card6
    } else {
        R.string.amount_normal to R.color.card1
    }
    text = context.getString(res, amount.toMoneyFormatted())
    setTextColor(ContextCompat.getColor(context, color))
}

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

fun ChipGroup.setProductCategoriesChip(categories: List<Category>) {
    categories.forEach {
        val chip = Chip(context).apply {
            text = it.categoryName.capitalizeFirstChar()
        }
        addView(chip)
    }
}

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

fun TextView.setOrderDebtSummary(newOrder: NewOrder) {
    val totalPaid = if (newOrder.newDebt != null) {
        newOrder.total - (newOrder.newDebt?.amount ?: 0F)
    } else {
        newOrder.total
    }
    text = context.getString(
        R.string.order_debt_amount_summary,
        totalPaid.toString(),
        newOrder.total.toString()
    )
}

fun TextView.setTraderType(type: String) {
    val res = when (type) {
        Constants.CLIENT -> R.string.trader_type_client
        Constants.SUPPLIER -> R.string.trader_type_supplier
        else -> R.string.trader_type_other
    }
    setText(res)
}

fun RadioGroup.setSelectedTraderType(type: String) {
    val checkedId = when (type) {
        Constants.CLIENT -> R.id.client_trader_type_radio_button
        Constants.SUPPLIER -> R.id.supplier_trader_type_radio_button
        else -> R.id.other_trader_type_radio_button
    }
    check(checkedId)
}

fun TextView.setTextOrDefault(data: String?) {
    text = if (data.isNullOrEmpty()) "-" else data
}

fun TextView.setTextOrDefault(data: List<*>?) {
    if (data.isNullOrEmpty()) text = "-"
}

fun TextView.setTextOrDefault(data: Number?) {
    text = if (data != null && data.toInt() != 0) context.getString(
        R.string.price_template,
        data.toFloat().toMoneyFormatted()
    ) else "-"
}

fun EditText.setNumberIfNotZero(data: Number) {
    if (data.toInt() != 0) setText(data.toString())
}

fun DonutChartView.setTrafficDonutChart(data: List<Traffic>?) {

    animation.duration = 1000L

    donutColors = intArrayOf(
        Color.parseColor("#FBB449"),
        Color.parseColor("#2D8EFF")
    )

    val donutData = data.takeIf { !it.isNullOrEmpty() }?.first()?.let {
        donutTotal = it.purchases + it.sales
        listOf(it.purchases, it.sales)
    } ?: emptyList()

    animate(donutData)
}

fun TextView.setTrafficRevenue(data: List<Traffic>?) {
    val revenue = data.takeIf { !it.isNullOrEmpty() }?.first()?.revenue() ?: 0F

    var revenueString = revenue.toMoneyFormatted()
    if (revenue >= 0) revenueString = "+$revenueString"

    text = context.getString(R.string.amount_normal, revenueString)
}

fun TextView.setCompareTrafficRevenue(data: List<Traffic>?) {
    when {
        data.isNullOrEmpty() -> {
            setText(R.string.default_traffic_revenue_comparison)
        }

        data.size == 1 -> {
            val revenue = data.first().revenue()
            val percentage = if (revenue < 0) "- 100" else "+ 100"
            text = context.getString(
                R.string.traffic_revenue_comparison,
                percentage,
                revenue.toMoneyFormatted()
            )
        }

        else -> {
            val firstRevenue = data[0].revenue()
            val secondRevenue = data[1].revenue()
            val revenue = firstRevenue - secondRevenue

            val percentage = ((firstRevenue * 100 / secondRevenue).toInt() - 100).absoluteValue
            val percentageString = if (revenue < 0) "- $percentage" else "+ $percentage"
            text = context.getString(
                R.string.traffic_revenue_comparison,
                percentageString,
                revenue.absoluteValue.toMoneyFormatted()
            )
        }
    }
}

fun TextView.setTrafficRevenuePercentage(data: List<Traffic>?) {
    val percentage = data.takeIf { !it.isNullOrEmpty() }?.first()?.let {
        (it.sales * 100 / (it.purchases + it.sales)).toInt()
    } ?: 0
    text = context.getString(R.string.number_percentage, percentage)
}

fun TextView.setEventMessageOrDefault(message: String?) {
    text =
        if (message.isNullOrBlank()) context.getString(R.string.action_add_event_content) else message
}

fun EditText.setProductRecordEntryPrice(newRecord: NewRecord) {
    hint = "$${newRecord.productUnitPrice}"
}

fun EditText.setProductRecordEntryStock(newRecord: NewRecord) {
    hint = "+${newRecord.currentStock}"
}