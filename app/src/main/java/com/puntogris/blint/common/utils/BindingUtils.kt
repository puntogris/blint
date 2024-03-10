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
fun setImageFullSize(imageView: ImageView, image: String) {
    if (image.isNotEmpty()) {
        GlideApp.with(imageView.context)
            .load(image)
            .transform()
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade(Constants.CROSS_FADE_DURATION))
            .into(imageView)
    } else {
        Glide.with(imageView.context)
            .clear(imageView)
        imageView.setBackgroundResource(R.color.color_on_primary)
    }
}

fun setProfileImage(imageView: ImageView, image: String?) {
    if (!image.isNullOrEmpty()) {
        GlideApp.with(imageView.context)
            .load(image)
            .circleCrop()
            .into(imageView)
    }
}

fun setProductPrices(textView: TextView, product: Product) {
    textView.text = textView.context.getString(
        R.string.product_buy_sell_prices_template,
        product.buyPrice.toMoneyFormatted(),
        product.sellPrice.toMoneyFormatted()
    )
}

fun setUserDataImage(imageView: ImageView, image: String?) {
    Glide.with(imageView.context)
        .load(image ?: R.drawable.ic_baseline_account_circle_24)
        .circleCrop()
        .into(imageView)
}

fun setDateFromFirebaseUser(textView: TextView, date: OffsetDateTime?) {
    if (date != null) {
        textView.text = date.getDateWithTimeFormattedString()
    }
}

fun setCapitalizeFirstChar(textView: TextView, text: String?) {
    textView.text = text?.capitalizeFirstChar()
}

fun setCapitalizeWord(textView: TextView, text: String) {
    textView.text = text.split(" ").joinToString(" ") { it.capitalizeFirstChar() }
}

fun setNumberToMoneyString(textView: TextView, number: Float) {
    textView.text = textView.context.getString(R.string.amount_normal, number.toMoneyFormatted())
}

fun setTotalOrderWithDetails(textView: TextView, order: OrderWithRecords) {
    val total = order.order.total.toString()
    textView.text = if (order.debt != null) {
        textView.context.getString(R.string.order_with_debt, total, order.debt?.amount.toString())
    } else {
        textView.context.getString(R.string.order_fully_paid, total)
    }
}

fun setDateFromTimestampWithTime(textView: TextView, timestamp: OffsetDateTime) {
    textView.text = timestamp.getDateWithTimeFormattedString()
}

fun setAmountSymbolWithRecordType(textView: TextView, type: String) {
    textView.text = if (type == IN || type == INITIAL) "+" else "-"
}

fun setRecordTypeString(textView: TextView, type: String) {
    textView.setText(
        if (type == IN || type == INITIAL) R.string.in_entry
        else R.string.out_entry
    )
}

fun setDateFromTimestamp(textView: TextView, timestamp: OffsetDateTime?) {
    timestamp?.let {
        textView.text = it.getDateFormattedString()
    }
}

fun setRecordType(view: View, type: String) {
    val color = if (type == IN || type == INITIAL) R.color.card6 else R.color.card1
    view.backgroundTintList =
        ColorStateList.valueOf(ResourcesCompat.getColor(view.resources, color, null))
}

fun Chip.setExternalChipName(name: String) {
    if (name.isNotEmpty()) {
        text = name.capitalizeFirstChar()
    } else {
        setText(R.string.not_specified)
        isEnabled = false
    }
}

fun setExternalName(textView: TextView, name: String) {
    textView.text = if (name.isNotEmpty()) {
        name.capitalizeFirstChar()
    } else {
        textView.context.getString(R.string.enter_trader_hint)
    }
}


fun setOrderDebtOrDefault(textView: TextView, newOrder: NewOrder) {
    textView.text = newOrder.newDebt?.amount?.let {
        textView.context.getString(R.string.debt_of_template, it.toMoneyFormatted())
    } ?: textView.context.getString(R.string.enter_debt_hint)
}

fun setEventStatus(textView: TextView, status: String) {
    textView.setText(if (status == PENDING) R.string.pending else R.string.finished)
}

fun setEventStatusColor(view: View, status: String) {
    val color = if (status == PENDING) R.color.card7 else R.color.card6
    view.backgroundTintList =
        ColorStateList.valueOf(ResourcesCompat.getColor(view.resources, color, null))
}

fun setOrderNumberTitle(textView: TextView, number: Int) {
    textView.text = textView.context.getString(R.string.order_number, number)
}

fun setDebtColor(textView: TextView, amount: Float) {
    val (res, color) = if (amount >= 0) {
        R.string.amount_debt_positive to R.color.card6
    } else {
        R.string.amount_normal to R.color.card1
    }
    textView.text = textView.context.getString(res, amount.toMoneyFormatted())
    textView.setTextColor(ContextCompat.getColor(textView.context, color))
}

fun setDebtColorWithLimit(textView: TextView, amount: Float) {
    val newAmount =
        if (amount > 1000000) (amount / 1000000).toMoneyFormatted() + "M." else amount.toMoneyFormatted()
    if (amount >= 0) {
        textView.text = textView.context.getString(R.string.amount_debt_positive, newAmount)
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.card6))
    } else {
        textView.text = textView.context.getString(R.string.amount_normal, newAmount)
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.card1))
    }
}

fun setProductCategoriesChip(chipGroup: ChipGroup, categories: List<Category>) {
    categories.forEach {
        val chip = Chip(chipGroup.context).apply {
            text = it.categoryName.capitalizeFirstChar()
        }
        chipGroup.addView(chip)
    }
}

fun setProductSuppliersChips(chipGroup: ChipGroup, suppliers: List<Trader>) {
    suppliers.forEach {
        val chip = Chip(chipGroup.context).apply {
            text = it.name.capitalizeFirstChar()
        }
        chipGroup.addView(chip)
    }
}

fun setDateOrError(textView: TextView, timeInMillis: Long) {
    textView.text = if (timeInMillis == 0L) {
        textView.context.getString(R.string.no_backup_found)
    } else {
        Date(timeInMillis).getDateWithTimeFormattedString()
    }
}

fun setOrderDebtSummary(textView: TextView, newOrder: NewOrder) {
    val totalPaid = if (newOrder.newDebt != null) {
        newOrder.total - (newOrder.newDebt?.amount ?: 0F)
    } else {
        newOrder.total
    }
    textView.text = textView.context.getString(
        R.string.order_debt_amount_summary,
        totalPaid.toString(),
        newOrder.total.toString()
    )
}

fun setTraderType(textView: TextView, type: String) {
    val res = when (type) {
        Constants.CLIENT -> R.string.trader_type_client
        Constants.SUPPLIER -> R.string.trader_type_supplier
        else -> R.string.trader_type_other
    }
    textView.setText(res)
}

fun setSelectedTraderType(radioGroup: RadioGroup, type: String) {
    val checkedId = when (type) {
        Constants.CLIENT -> R.id.client_trader_type_radio_button
        Constants.SUPPLIER -> R.id.supplier_trader_type_radio_button
        else -> R.id.other_trader_type_radio_button
    }
    radioGroup.check(checkedId)
}

fun setTextOrDefault(textView: TextView, data: String?) {
    textView.text = if (data.isNullOrEmpty()) "-" else data
}

fun setTextOrDefault(textView: TextView, data: List<*>?) {
    if (data.isNullOrEmpty()) textView.text = "-"
}

fun setTextOrDefault(textView: TextView, data: Number?) {
    textView.text = if (data != null && data.toInt() != 0) textView.context.getString(
        R.string.price_template,
        data.toFloat().toMoneyFormatted()
    ) else "-"
}

fun setNumberIfNotZero(editText: EditText, data: Number) {
    if (data.toInt() != 0) editText.setText(data.toString())
}

fun setTrafficDonutChart(donutChartView: DonutChartView, data: List<Traffic>?) {
    donutChartView.animation.duration = 1000L

    donutChartView.donutColors = intArrayOf(
        Color.parseColor("#FBB449"),
        Color.parseColor("#2D8EFF")
    )

    val donutData = data.takeIf { !it.isNullOrEmpty() }?.first()?.let {
        donutChartView.donutTotal = it.purchases + it.sales
        listOf(it.purchases, it.sales)
    } ?: emptyList()

    donutChartView.animate(donutData)
}

fun setTrafficRevenue(textView: TextView, data: List<Traffic>?) {
    val revenue = data.takeIf { !it.isNullOrEmpty() }?.first()?.revenue() ?: 0F

    var revenueString = revenue.toMoneyFormatted()
    if (revenue >= 0) revenueString = "+$revenueString"

    textView.text = textView.context.getString(R.string.amount_normal, revenueString)
}

fun setCompareTrafficRevenue(textView: TextView, data: List<Traffic>?) {
    when {
        data.isNullOrEmpty() -> {
            textView.setText(R.string.default_traffic_revenue_comparison)
        }

        data.size == 1 -> {
            val revenue = data.first().revenue()
            val percentage = if (revenue < 0) "- 100" else "+ 100"
            textView.text = textView.context.getString(
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
            textView.text = textView.context.getString(
                R.string.traffic_revenue_comparison,
                percentageString,
                revenue.absoluteValue.toMoneyFormatted()
            )
        }
    }
}

fun setTrafficRevenuePercentage(textView: TextView, data: List<Traffic>?) {
    val percentage = data.takeIf { !it.isNullOrEmpty() }?.first()?.let {
        (it.sales * 100 / (it.purchases + it.sales)).toInt()
    } ?: 0
    textView.text = textView.context.getString(R.string.number_percentage, percentage)
}

fun setEventMessageOrDefault(textView: TextView, message: String?) {
    textView.text = if (message.isNullOrBlank()) {
        textView.context.getString(R.string.action_add_event_content)
    } else {
        message
    }
}

fun setProductRecordEntryPrice(editText: EditText, newRecord: NewRecord) {
    editText.hint = "$${newRecord.productUnitPrice}"
}

fun setProductRecordEntryStock(editText: EditText, newRecord: NewRecord) {
    editText.hint = "+${newRecord.currentStock}"
}