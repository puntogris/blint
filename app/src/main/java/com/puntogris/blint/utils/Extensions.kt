package com.puntogris.blint.utils

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Parcelable
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.DimenRes
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.maxkeppeler.sheets.core.SheetStyle
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.NavigationDirections
import com.puntogris.blint.R
import com.puntogris.blint.model.Event
import com.puntogris.blint.model.product.Product
import com.puntogris.blint.ui.main.SetupUiListener
import com.puntogris.blint.utils.types.EventUi
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

fun Any.bindDimen(context: Context, @DimenRes id: Int) = lazy(LazyThreadSafetyMode.NONE) {
    context.resources.getDimension(id)
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

fun Flow<PagingData<Event>>.toEventUiFlow(): Flow<PagingData<EventUi>> {
    return map { pagingData -> pagingData.map {
        EventUi.EventItem(it) } }
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

fun Fragment.isDarkThemeOn() =
    (resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)

fun Activity.isDarkThemeOn() =
    (resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)

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

fun Activity.launchWebBrowserIntent(uri: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        startActivity(intent)
    } catch (e: Exception) {
        (this as SetupUiListener).showSnackBar(getString(R.string.snack_ups_visit_blint))
    }
}

inline val Context.screenWidth: Int
    get() =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            Point().also { display?.getRealSize(it) }.x
        } else {
            @Suppress("DEPRECATION")
            Point().also {
                (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(
                    it
                )
            }.x
        }

inline val View.screenWidth: Int
    get() = context!!.screenWidth

inline val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    ).toInt()

inline val Float.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics
    )

inline fun getValueAnimator(
    forward: Boolean = true,
    duration: Long,
    interpolator: TimeInterpolator,
    crossinline updateListener: (progress: Float) -> Unit
): ValueAnimator {
    val a =
        if (forward) ValueAnimator.ofFloat(0f, 1f)
        else ValueAnimator.ofFloat(1f, 0f)
    a.addUpdateListener { updateListener(it.animatedValue as Float) }
    a.duration = duration
    a.interpolator = interpolator
    return a
}

fun LottieAnimationView.playAnimationOnce(@RawRes animation: Int) {
    setAnimation(animation)
    repeatCount = 0
    playAnimation()
}

fun Fragment.showOrderPickerAndNavigate(product: Product? = null) {
    OptionsSheet().build(requireContext()) {
        displayMode(DisplayMode.GRID_HORIZONTAL)
        style(SheetStyle.BOTTOM_SHEET)
        with(
            Option(
                R.drawable.ic_baseline_speed_24,
                this@showOrderPickerAndNavigate.getString(R.string.create_simple_order)
            ),
            Option(
                R.drawable.ic_baseline_timer_24,
                this@showOrderPickerAndNavigate.getString(R.string.create_detailed_order)
            ),
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
    }.show(parentFragmentManager, "")
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

fun Fragment.generateQRImage(
    code: String,
    width: Int,
    height: Int,
    isPdf: Boolean = false
): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, width, height)
    val widthB = bitMatrix.width
    val heightB = bitMatrix.height
    val bitmap = Bitmap.createBitmap(widthB, heightB, Bitmap.Config.RGB_565)

    val darkThemeOn = isDarkThemeOn()
    val background =
        if (isPdf) getQrCodeWithTheme(false, isPdf = true) else getQrCodeWithTheme(darkThemeOn)
    val qrCode = if (isPdf) getQrCodeWithTheme(true) else getQrCodeWithTheme(!darkThemeOn)

    for (x in 0 until widthB) {
        for (y in 0 until heightB) {
            bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) qrCode else background)
        }
    }
    return bitmap
}

fun Fragment.getQrCodeWithTheme(darkThemeOn: Boolean, isPdf: Boolean = false): Int {
    val color = if (darkThemeOn) R.color.nightBackground
    else {
        if (isPdf) R.color.white
        else R.color.dayBackground
    }
    return ContextCompat.getColor(requireContext(), color)
}

fun String.capitalizeFirstChar() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

fun <T> Fragment.onBackStackLiveData(key: String, observer: Observer<T>) {
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)
        ?.observe(viewLifecycleOwner, observer)
}

inline val Fragment.UiInterface: SetupUiListener
    get() = (requireActivity() as SetupUiListener)

inline fun <T : Parcelable> Fragment.takeArgsIfNotNull(key: String, action: (T) -> (Unit)) {
    arguments?.takeIf { it.containsKey(key) }?.apply {
        getParcelable<T>(key)?.let {
            action(it)
        }
    }
}

fun Fragment.registerToolbarBackButton(toolbar: MaterialToolbar) {
    toolbar.setNavigationOnClickListener {
        findNavController().navigateUp()
    }
}
