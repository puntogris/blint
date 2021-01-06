package com.puntogris.blint.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.puntogris.blint.R
import com.puntogris.blint.databinding.ActivityMainBinding
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var clicked = false

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Blint)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupNavigation()
        supportActionBar?.elevation  = 0F

        binding.bottomAppBar.setNavigationOnClickListener {
            if (navController.currentDestination?.id != R.id.mainFragment){
                navController.navigate(R.id.mainFragment)
            }
            if (clicked) onParentFabClicked()
        }

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.scannerFragment -> {
                    navController.navigate(R.id.scannerFragment)
                    true
                }
                R.id.preferencesFragment -> {
                    navController.navigate(R.id.preferencesFragment)
                    true
                }
                else -> false
            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.addFav.changeIconFromDrawable(R.drawable.ic_baseline_add_24)
            binding.addFav.setOnClickListener{ onParentFabClicked()}
            binding.bottomAppBar.performShow()
            if (clicked) onParentFabClicked()
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

    private fun setupNavigation(){
        navController = getNavController()
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }
}