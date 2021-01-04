package com.puntogris.blint.utils

import android.graphics.Color
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.puntogris.blint.R
import com.puntogris.blint.model.MenuCard
import timber.log.Timber

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

fun Fragment.getParentFab(): FloatingActionButton =
    requireActivity().findViewById(R.id.addFav)

fun FloatingActionButton.changeIconFromDrawable(icon: Int){
    setImageDrawable(ContextCompat.getDrawable(context, icon))
}

fun AppCompatActivity.getNavController() =
    (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

fun Fragment.showSnackBarAboveFab(message: String){
    val snackLayout = this.requireActivity().findViewById<View>(android.R.id.content)
    Snackbar
            .make(snackLayout, message, Snackbar.LENGTH_LONG)
            .setAnchorView(getParentFab())
            .show()
}

fun Fragment.createSnackBar(message: String): Snackbar{
    val snackLayout = this.requireActivity().findViewById<View>(android.R.id.content)
    return Snackbar.make(snackLayout, message, Snackbar.LENGTH_LONG)
}

fun Fragment.showSnackBar(message: String){
    val snackLayout = this.requireActivity().findViewById<View>(android.R.id.content)
    Snackbar.make(snackLayout, message, Snackbar.LENGTH_LONG)
}

