package com.puntogris.blint.ui.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.*
import com.puntogris.blint.R
import com.puntogris.blint.databinding.ActivityMainBinding
import com.puntogris.blint.ui.base.BaseActivity
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main){

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var clicked = false

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }

    override fun preInitViews() {
        setTheme(R.style.Theme_Blint)
    }

    override fun initializeViews() {
        supportActionBar?.elevation  = 0F
        setUpNavigation()
        delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
    }

    private fun setUpNavigation(){
        navController = getNavController()

        binding.run {
            navController.addOnDestinationChangedListener(this@MainActivity)
        }
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.mainFragment,
            R.id.registerBusinessFragment,
            R.id.preferencesFragment
        ))

        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomAppBar.apply {
            setNavigationOnClickListener {
                if (navController.currentDestination?.id != R.id.mainFragment){
                    navController.navigate(R.id.mainFragment)
                }
                if (clicked) onParentFabClicked()
            }
            setOnMenuItemClickListener(this@MainActivity)
        }
    }

    private fun onParentFabClicked(){
        setAnimation(clicked)
        setVisibility(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean){
        binding.apply {
            if (!clicked){
                addClientFab.apply {
                    visible()
                    setOnClickListener { navController.navigate(R.id.clientFragment) }
                }
                addProductFab.apply {
                    visible()
                    setOnClickListener { navController.navigate(R.id.productFragment) }
                }
                addSuplierFab.apply {
                    visible()
                    setOnClickListener { navController.navigate(R.id.supplierFragment) }
                }
            }else listOf(addClientFab, addProductFab, addSuplierFab).makeInvisible()
        }
    }

    private fun setAnimation(clicked: Boolean){
        binding.apply {
            if (!clicked){
                addFav.startAnimation(rotateOpen)
                listOf(addClientFab, addProductFab, addSuplierFab).setGroupAnimation(fromBottom)
            }else{
                addFav.startAnimation(rotateClose)
                listOf(addClientFab, addProductFab, addSuplierFab).setGroupAnimation(toBottom)
            }
        }
    }

    private fun setClickable(clicked: Boolean){
        binding.apply {
            if (!clicked) listOf(addClientFab, addProductFab, addSuplierFab).setGroupClickable(true)
            else listOf(addClientFab, addProductFab, addSuplierFab).setGroupClickable(false)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun hideBottomAppBar() {
        binding.run {
            bottomAppBar.performHide()
            // Get a handle on the animator that hides the bottom app bar so we can wait to hide
            // the fab and bottom app bar until after it's exit animation finishes.
            bottomAppBar.animate().setListener(object : AnimatorListenerAdapter() {
                var isCanceled = false
                override fun onAnimationEnd(animation: Animator?) {
                    if (isCanceled) return

                    // Hide the BottomAppBar to avoid it showing above the keyboard
                    // when composing a new email.
                    bottomAppBar.visibility = View.GONE
                    binding.addFav.visibility = View.INVISIBLE
                }
                override fun onAnimationCancel(animation: Animator?) {
                    isCanceled = true
                }
            })
        }
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        when(destination.id){
            R.id.registerBusinessFragment ->{
                hideBottomAppBar()
            }
        }
//        binding.addFav.changeIconFromDrawable(R.drawable.ic_baseline_add_24)
//        binding.addFav.setOnClickListener{ onParentFabClicked()}
//        binding.bottomAppBar.performShow()
//        if (clicked) onParentFabClicked()
    }

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.scannerFragment -> {
                navController.navigate(R.id.scannerFragment)
            }
            R.id.preferencesFragment -> {
                navController.navigate(R.id.preferencesFragment)
            }
        }
        return true
    }
}