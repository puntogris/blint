package com.puntogris.blint.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.puntogris.blint.R
import java.text.NumberFormat
import java.util.*

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
    return if (text.isNotBlank()) text.toInt() else 0
}


fun Fragment.getParentFab(): FloatingActionButton =
    requireActivity().findViewById(R.id.addFav)

fun FloatingActionButton.changeIconFromDrawable(icon: Int){
    setImageDrawable(ContextCompat.getDrawable(context, icon))
}

fun AppCompatActivity.getNavController() =
    (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

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

fun Fragment.createShortSnackBar(message: String): Snackbar{
    val snackLayout = this.requireActivity().findViewById<View>(android.R.id.content)
    return Snackbar.make(snackLayout, message, Snackbar.LENGTH_SHORT)
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
