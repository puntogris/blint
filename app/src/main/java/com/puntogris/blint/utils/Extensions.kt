package com.puntogris.blint.utils

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.puntogris.blint.R
import com.puntogris.blint.model.MenuCard

fun AppCompatActivity.getNavController() =
    (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

fun Fragment.createSnackBar(message: String){
    val snackLayout = this.requireActivity().findViewById<View>(android.R.id.content)
    Snackbar.make(snackLayout, message, Snackbar.LENGTH_LONG).show()
}

@BindingAdapter("menuCardColor")
fun CardView.setBackgroundColor(menuCard: MenuCard){
    setCardBackgroundColor(context.getColor(menuCard.color))
    if (menuCard.id == 6) findViewById<ImageView>(R.id.imageView).visibility = View.GONE
}

@BindingAdapter("menuCardIcon")
fun ImageView.setMenuCardIcon(menuCard: MenuCard){
    if (menuCard.id == 6) visibility = View.GONE
    else{
        setImageDrawable(ContextCompat.getDrawable(context, menuCard.icon))
        setColorFilter(Color.WHITE)
    }
}