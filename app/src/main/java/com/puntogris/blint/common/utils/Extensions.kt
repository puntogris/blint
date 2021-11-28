package com.puntogris.blint.common.utils

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import android.net.Uri
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.Timestamp
import com.maxkeppeler.sheets.core.SheetStyle
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.NavigationDirections
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.types.EventUi
import com.puntogris.blint.feature_store.domain.model.Event
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.presentation.main.SetupUiListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun EditText.getString() = text.toString()

fun EditText.getInt(): Int {
    val text = text.toString()
    return if (text.isEmpty()) 0 else text.toInt()
}

fun EditText.getFloat(): Float {
    val text = text.toString()
    return if (text.isNotBlank()) text.toFloat() else 0F
}

fun FloatingActionButton.changeIconFromDrawable(icon: Int) {
    setImageDrawable(ContextCompat.getDrawable(context, icon))
}

fun AppCompatActivity.getNavController() =
    (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

fun AppCompatActivity.getNavHostFragment() =
    (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)

fun BottomSheetDialogFragment.showSackBarAboveBottomSheet(message: String) {
    Snackbar.make(
        dialog?.window!!.decorView,
        message,
        Snackbar.LENGTH_SHORT
    ).show()
}

fun Float.toUSDFormatted(): String = NumberFormat.getCurrencyInstance(Locale.US).format(this)

fun String.containsInvalidCharacters() = !all { it.isLetter() || it.isWhitespace() }

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Fragment.hideKeyboard() {
    view?.let { requireActivity().hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Float.toMoneyFormatted(removeSuffix: Boolean = false): String {
    return DecimalFormat("###,###,##0.00").format(this).apply {
        if (removeSuffix) {
            return this.removeSuffix(".00")
        }
    }
}

fun Date.getDateFormattedString() =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(this).toString()

fun Date.getDateFormattedStringUnderLine() =
    SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(this).toString()

fun Date.getDateWithTimeFormattedString() =
    SimpleDateFormat("dd/MM/yyyy - h:mm a", Locale.getDefault()).format(this).toString()

fun Fragment.getDatabasePath(): String =
    requireActivity().getDatabasePath("blint_database").absolutePath

fun Timestamp.getMonthAndYeah() =
    SimpleDateFormat("MMMM - yyyy", Locale.getDefault()).format(this.toDate()).toString()

fun Timestamp.getMonth() =
    SimpleDateFormat("MMMM", Locale.getDefault()).format(this.toDate()).toString()

fun Flow<PagingData<Event>>.toEventUi(): Flow<PagingData<EventUi>> {
    return map { pagingData ->
        pagingData.map {
            EventUi.EventItem(it)
        }
    }
        .map {
            it.insertSeparators { before, after ->

                if (after == null) {
                    return@insertSeparators null
                }

                if (before == null) {
                    EventUi.SeparatorItem(after.event.timestamp.getMonth().capitalizeFirstChar())
                }

                if (before?.event?.timestamp?.getMonthAndYeah() != after.event.timestamp.getMonthAndYeah()) {
                    EventUi.SeparatorItem(after.event.timestamp.getMonth().capitalizeFirstChar())
                } else {
                    null
                }
            }
        }
}

fun String.getDateWithFileName() =
    SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault()).format(Date())
        .toString() + "_$this.xls"

fun Fragment.launchWebBrowserIntent(uri: String, packageName: String? = null) {
    try {
        Intent(Intent.ACTION_VIEW).let {
            it.data = Uri.parse(uri)
            if (packageName != null) it.setPackage(packageName)
            startActivity(it)
        }

    } catch (e: Exception) {
        UiInterface.showSnackBar(getString(R.string.snack_ups_visit_blint))
    }
}


fun LottieAnimationView.playAnimationOnce(@RawRes animation: Int) {
    visible()
    setAnimation(animation)
    repeatCount = 0
    playAnimation()
}

fun LottieAnimationView.playAnimationInfinite(@RawRes animation: Int) {
    visible()
    setAnimation(animation)
    repeatCount = LottieDrawable.INFINITE
    playAnimation()
}

fun Fragment.showOrderPickerAndNavigate(product: Product? = null) {
    OptionsSheet().show(requireParentFragment().requireContext()) {
        displayMode(DisplayMode.GRID_HORIZONTAL)
        style(SheetStyle.BOTTOM_SHEET)
        with(
            Option(R.drawable.ic_baseline_speed_24,R.string.create_simple_order),
            Option(R.drawable.ic_baseline_timer_24, R.string.create_detailed_order),
        )
        onPositive { index: Int, _ ->
            if (index == 0) {
                val action = NavigationDirections.actionGlobalSimpleOrderBottomSheet(product!!)
                findNavController().navigate(action)
            } else {
                val action = NavigationDirections.actionGlobalNewOrderGraphNav(product)
                findNavController().navigate(action)
            }
        }
    }
}

inline fun Fragment.launchAndRepeatWithViewLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            block()
        }
    }
}

fun String.capitalizeFirstChar() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

inline val Fragment.UiInterface: SetupUiListener
    get() = (requireActivity() as SetupUiListener)

fun Fragment.registerToolbarBackButton(toolbar: MaterialToolbar) {
    toolbar.setNavigationOnClickListener {
        findNavController().navigateUp()
    }
}

inline fun TabLayout.addOnTabSelectedListener(crossinline block: (Int) -> Unit) {
    addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            block(tab?.position ?: return)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) = Unit
        override fun onTabReselected(tab: TabLayout.Tab?) = Unit
    })
}

fun <T> List<T>.copyAndAdd(item: T): List<T> {
    return this.toMutableList().apply { add(item) }
}

fun <T> List<T>.copyAndRemove(item: T): List<T> {
    return this.toMutableList().apply { remove(item) }
}
