package com.puntogris.blint.utils

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Point
import android.net.Uri
import android.opengl.Visibility
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.maxkeppeler.sheets.core.SheetStyle
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.nex3z.notificationbadge.NotificationBadge
import com.puntogris.blint.NavigationDirections
import com.puntogris.blint.R
import com.puntogris.blint.model.Event
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.main.MainActivity
import com.puntogris.blint.ui.nav.BottomNavDrawerFragment
import com.puntogris.blint.ui.nav.ShowHideFabStateAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

fun View.gone(){
    visibility = View.GONE
}

fun View.visible(){
    visibility = View.VISIBLE
}
fun View.invisible(){
    visibility = View.INVISIBLE
}

fun EditText.getString() = text.toString()

fun EditText.getInt(): Int{
    val text = text.toString()
    return if(text.isEmpty()) 0 else text.toInt()
}

fun EditText.getFloat(): Float{
    val text = text.toString()
    return if (text.isNotBlank()) text.toFloat() else 0F
}

fun Fragment.getParentFab(): FloatingActionButton =
    requireActivity().findViewById(R.id.mainFab)

fun Fragment.getParentBottomAppBar(): BottomAppBar =
    requireActivity().findViewById(R.id.bottomAppBar)

fun Fragment.setParentFabAlignment(alignment: Int){
    requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar).fabAlignmentMode = alignment
}

fun FloatingActionButton.changeIconFromDrawable(icon: Int){
    setImageDrawable(ContextCompat.getDrawable(context, icon))
}

fun AppCompatActivity.getNavController() =
    (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

fun AppCompatActivity.getNavHostFragment() =
    (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)

fun Fragment.showLongSnackBarAboveFab(message: String){
    val snackLayout = this.requireActivity().findViewById<View>(android.R.id.content)
    Snackbar
            .make(snackLayout, message, Snackbar.LENGTH_LONG)
            .setAnchorView(getParentFab())
            .show()
}

fun Fragment.createLongSnackBarAboveFab(message: String): Snackbar{
    val snackLayout = this.requireActivity().findViewById<View>(android.R.id.content)
    val snack = Snackbar
        .make(snackLayout, message, Snackbar.LENGTH_LONG)
        snack.anchorView = getParentFab()
    return snack
}

fun Activity.showLongSnackBarAboveFab(message: String){
    val snackLayout = findViewById<View>(android.R.id.content)
    Snackbar
        .make(snackLayout, message, Snackbar.LENGTH_LONG)
        .setAnchorView(findViewById(R.id.mainFab)).show()
}

fun Fragment.createLongSnackBar(message: String): Snackbar{
    val snackLayout = this.requireActivity().findViewById<View>(android.R.id.content)
    return Snackbar.make(snackLayout, message, Snackbar.LENGTH_LONG)
}

fun BottomSheetDialogFragment.showSackBarAboveBottomSheet(message:String){
    Snackbar.make(
        dialog?.window!!.decorView,
        message,
        Snackbar.LENGTH_SHORT
    ).show()
}

fun Fragment.createShortSnackBar(message: String): Snackbar{
    val snackLayout = this.requireActivity().findViewById<View>(android.R.id.content)
    return Snackbar.make(snackLayout, message, Snackbar.LENGTH_SHORT)
}

fun Fragment.showShortSnackBar(message: String){
    val snackLayout = this.requireActivity().findViewById<View>(android.R.id.content)
    Snackbar.make(snackLayout, message, Snackbar.LENGTH_SHORT).show()
}

fun Context.showLongSnackBar(message: String){
    val snackLayout: View = (this as Activity).findViewById(android.R.id.content)
    Snackbar.make(snackLayout, message, Snackbar.LENGTH_LONG).show()

}

fun Context.dpToPx(dp : Float) : Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics
    )
}

fun Context.pxToDp(px : Int) : Float {
    val displayMetrics = resources.displayMetrics
    return px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun Float.toUSDFormatted(): String = NumberFormat.getCurrencyInstance(Locale.US).format(this)

fun String.containsInvalidCharacters() = !all { it.isLetter() }

fun String.isLengthInvalid(validLength: Int) = length < validLength

fun Fragment.createNewChipAndAddItToGroup(name: String, chipGroup: ChipGroup) =
    Chip(requireContext()).apply {
        text = name
        isCloseIconVisible = true
        closeIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_close_24, null)
        setOnClickListener { chipGroup.removeView(it) }
        chipGroup.addView(this)
    }

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

fun HashMap<String, String>.containsImage() = any { it.value.isNotEmpty() }

fun ViewGroup.inflate(@LayoutRes res: Int): View {
    return LayoutInflater.from(context)
        .inflate(res, this, false)
}

fun Float.toMoneyFormatted(removeSuffix : Boolean = false) : String {
    return DecimalFormat("###,###,##0.00").format(this).apply {
        if(removeSuffix){
            return this.removeSuffix(".00")
        }
    }
}

fun Date.getDateFormattedString() =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(this).toString()


fun Date.getDateWithTimeFormattedString() =
    SimpleDateFormat("dd/MM/yyyy - h:mm a", Locale.getDefault()).format(this).toString()

fun Fragment.getDatabasePath():String =
    requireActivity().getDatabasePath("blint_database").absolutePath

fun Date.getMonthWithYear() =
    SimpleDateFormat("MMMM - yyyy", Locale.getDefault()).format(this).toString()

fun Timestamp.getMonthAndYeah() =
    SimpleDateFormat("MMMM - yyyy", Locale.getDefault()).format(this.toDate()).toString()

fun Timestamp.getMonth() =
    SimpleDateFormat("MMMM", Locale.getDefault()).format(this.toDate()).toString()

fun Flow<PagingData<Event>>.toEventUiFlow():Flow<PagingData<EventUi>>{
    return map { pagingData -> pagingData.map { EventUi.EventItem(it) } }
        .map{
            it.insertSeparators { before, after ->

                if (after == null) {
                    return@insertSeparators null
                }

                if (before == null) {
                    EventUi.SeparatorItem(after.event.timestamp.getMonth().capitalize(Locale.getDefault()))
                }

                if (before?.event?.timestamp?.getMonthAndYeah() != after.event.timestamp.getMonthAndYeah()){
                    EventUi.SeparatorItem(after.event.timestamp.getMonth().capitalize(Locale.getDefault()))
                }else{
                    null
                }
            }
        }
}

fun String.getDateWithFileName() =
    SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault()).format(Date()).toString() + "_$this.xls"

fun View.isDarkThemeOn() =
    (resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)

fun Fragment.isDarkThemeOn() =
    (resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)

fun Activity.isDarkThemeOn() =
    (resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)

fun Fragment.launchWebBrowserIntent(uri: String){
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(uri)
    startActivity(intent)
}

inline val Context.screenWidth: Int
    get() = Point().also { (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(it) }.x

inline val View.screenWidth: Int
    get() = context!!.screenWidth

inline val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()

inline val Float.dp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)

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

fun Any.bindDimen(context: Context, @DimenRes id: Int) = lazy(LazyThreadSafetyMode.NONE) {
    context.resources.getDimension(id)
}

fun Fragment.showSnackBarVisibilityAppBar(text:String){
    getParentBottomAppBar().let {
        if (it.isVisible)
            createLongSnackBar(text).setAnchorView(it).show()
        else
            showShortSnackBar(text)
    }
}

fun Fragment.getParentToolbar(): Toolbar = requireActivity().findViewById(R.id.toolbar)

fun Fragment.getParentBadge(): NotificationBadge = requireActivity().findViewById(R.id.badge)

fun Fragment.setUpUi(showFab: Boolean = false,
                     showAppBar: Boolean = true,
                     showToolbar: Boolean = true,
                     showFabCenter: Boolean = true,
                     @DrawableRes fabIcon: Int = R.drawable.ic_baseline_add_24,
                     fabListener: View.OnClickListener? = null
){
    (requireActivity() as MainActivity).apply {
        findViewById<Toolbar>(R.id.toolbar).visibility = if (showToolbar) View.VISIBLE else View.GONE
        val fab = findViewById<FloatingActionButton>(R.id.mainFab)
        val appBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        if (showFab) {
            fab.apply {
                show()
                changeIconFromDrawable(fabIcon)
                setOnClickListener(fabListener)
            }
            appBar.fabAlignmentMode =
                if (showFabCenter) BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                else BottomAppBar.FAB_ALIGNMENT_MODE_END

        } else fab.hide()
        changeFabStateBottomSheet(showFab)
        if (showAppBar) {
            appBar.visible()
            appBar.performShow()
        } else appBar.gone()
    }
}

fun Activity.setToolbarAndStatusBarColor(color: Int){
    ContextCompat.getColor(this, color).apply {
        window.statusBarColor = this
        findViewById<Toolbar>(R.id.toolbar).setBackgroundColor(this)
    }
}

fun Fragment.setupStatusBarForLoginBackground(){
    getParentToolbar().setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorSecondary))
    val window = requireActivity().window
    window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorSecondary)
    if (!isDarkThemeOn()){
        val view = window.decorView
        view.setSystemUiVisibility(view.getSystemUiVisibility() and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv())
    }
}

fun LottieAnimationView.playAnimationOnce(@RawRes animation: Int){
    setAnimation(animation)
    repeatCount = 0
    playAnimation()
}

fun Fragment.showOrderPickerAndNavigate(product: Product? = null){
    OptionsSheet().build(requireContext()){
        displayMode(DisplayMode.GRID_HORIZONTAL)
        style(SheetStyle.DIALOG)
        with(
            Option(R.drawable.ic_baseline_speed_24, this@showOrderPickerAndNavigate.getString(R.string.create_simple_order)),
            Option(R.drawable.ic_baseline_timer_24, this@showOrderPickerAndNavigate.getString(R.string.create_detailed_order)),
        )
        onPositive { index: Int, _ ->
            if (index == 0){

            }else{
                val action = NavigationDirections.actionGlobalNewOrderGraphNav(product)
                findNavController().navigate(action)
            }
        }
    }.show(parentFragmentManager,"")
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