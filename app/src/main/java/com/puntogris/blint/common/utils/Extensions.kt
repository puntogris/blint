package com.puntogris.blint.common.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.TorchState
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.paging.*
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.Timestamp
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun EditText.getString() = text.toString()

fun EditText.getInt(): Int = text.toString().toIntOrNull() ?: 0

fun EditText.getFloat(): Float = text.toString().toFloatOrNull() ?: 0F

fun AppCompatActivity.getNavController() =
    (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

fun AppCompatActivity.getNavHostFragment() =
    (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)

fun String.containsInvalidCharacters() = !all { it.isLetter() || it.isWhitespace() }

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Fragment.hideKeyboard() {
    view?.let { requireActivity().hideKeyboard(it) }
}

fun DialogFragment.hideKeyboard() {
    requireDialog().currentFocus?.let {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            it.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

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

fun Timestamp.toOffsetDateTime(): OffsetDateTime {
    return OffsetDateTime.ofInstant(DateTimeUtils.toInstant(toDate()), ZoneId.systemDefault())
}

fun OffsetDateTime.getDateFormattedString(): String =
    format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

fun OffsetDateTime.getDateWithTimeFormattedString(): String =
    format(DateTimeFormatter.ofPattern("dd/MM/yyyy - h:mm a"))

fun OffsetDateTime.getMonthAndYeah(): String = format(DateTimeFormatter.ofPattern("MMMM - yyyy"))

fun Date.getDateWithTimeFormattedString() =
    SimpleDateFormat("dd/MM/yyyy - h:mm a", Locale.getDefault()).format(this).toString()

fun Flow<PagingData<Event>>.toEventUi(): Flow<PagingData<EventUi>> {
    return map { pagingData ->
        pagingData.map {
            EventUi.EventItem(it)
        }
    }.map {
        it.insertSeparators { before, after ->

            if (after == null) return@insertSeparators null

            if (before == null) {
                EventUi.SeparatorItem(after.event.timestamp.month.name)
            }

            if (before?.event?.timestamp?.getMonthAndYeah() != after.event.timestamp.getMonthAndYeah()) {
                EventUi.SeparatorItem(after.event.timestamp.month.name)
            } else {
                null
            }
        }
    }
}

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
        displayMode(DisplayMode.LIST)
        with(
            Option(R.drawable.ic_fi_rr_download, R.string.in_entry),
            Option(R.drawable.ic_fi_rr_upload, R.string.out_entry)
        )
        onPositive { index: Int, _ ->
            val type = if (index == 0) Constants.IN else Constants.OUT
            val action = NavigationDirections.actionGlobalNewOrderGraphNav(
                orderType = type,
                product = product
            )
            findNavController().navigate(action)
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

fun <T> List<T>.copyAndAdd(item: T): List<T> {
    return this.toMutableList().apply { add(item) }
}

fun <T> List<T>.copyAndRemove(item: T): List<T> {
    return this.toMutableList().apply { remove(item) }
}

inline fun PreferenceFragmentCompat.preference(key: String, block: Preference.() -> Unit) {
    findPreference<Preference>(key)?.apply {
        block(this)
    }
}

inline fun Preference.onClick(crossinline block: () -> Unit) {
    setOnPreferenceClickListener {
        block()
        true
    }
}

inline fun PreferenceFragmentCompat.onPreferenceChange(
    key: String,
    crossinline block: (Any) -> Unit
) {
    findPreference<Preference>(key)?.setOnPreferenceChangeListener { _, newValue ->
        block(newValue)
        true
    }
}

inline fun PreferenceFragmentCompat.preferenceOnClick(key: String, crossinline block: () -> Unit) {
    findPreference<Preference>(key)?.setOnPreferenceClickListener {
        block()
        true
    }
}

fun File.getUriFromProvider(context: Context): Uri {
    return FileProvider.getUriForFile(context, context.packageName, this)
}

fun NavController.navigateAndClearStack(destination: Int) {
    val nav = NavOptions.Builder().setPopUpTo(R.id.navigation, true).build()
    navigate(destination, null, nav)
}

fun Fragment.isDarkThemeOn() =
    (resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)


fun Camera.toggleFlash() {
    cameraControl.enableTorch(cameraInfo.torchState.value == TorchState.OFF)
}

suspend inline fun showEmptyUiOnEmptyAdapter(
    adapter: PagingDataAdapter<*, *>,
    view: View
) {
    adapter.loadStateFlow
        .distinctUntilChanged()
        .map { it.source.refresh }
        .filter { it is LoadState.NotLoading }
        .collect {
            view.isVisible = adapter.itemCount == 0
        }
}