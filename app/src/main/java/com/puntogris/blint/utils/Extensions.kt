package com.puntogris.blint.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.InsetDrawable
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.parser.IntegerParser
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.puntogris.blint.R
import java.text.DecimalFormat
import java.text.NumberFormat
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

fun List<View>.makeInvisible(){
    forEach { it.invisible() }
}

fun List<View>.setGroupClickable(isClickable: Boolean){
    forEach { it.isClickable = isClickable }
}

fun List<View>.setGroupAnimation(animation:Animation){
    forEach { it.startAnimation(animation) }
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
    requireActivity().findViewById(R.id.addFav)

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

fun Fragment.createLongSnackBar(message: String): Snackbar{
    val snackLayout = this.requireActivity().findViewById<View>(android.R.id.content)
    return Snackbar.make(snackLayout, message, Snackbar.LENGTH_LONG)
}

fun BottomSheetDialogFragment.showSackBarAboveBotomSheet(message:String){
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

fun Float.toUSDFormatted() : String {
    return NumberFormat.getCurrencyInstance(Locale.US).format(this)
}

fun String.containsInvalidCharacters() = !all { it.isLetter() }

fun String.isLengthInvalid(validLength: Int) = length < validLength


fun Fragment.createNewChipAndAddItToGroup(name: String, chipGroup: ChipGroup) =
    Chip(requireContext()).apply {
        text = "Item $name"
        isCloseIconVisible = true
        closeIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_close_24, null)
        setOnClickListener { chipGroup.removeView(it) }
        chipGroup.addView(this)
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
