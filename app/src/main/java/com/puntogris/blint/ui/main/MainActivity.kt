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
import com.puntogris.blint.utils.getNavController
import com.puntogris.blint.utils.invisible
import com.puntogris.blint.utils.visible
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
            navController.navigate(R.id.mainFragment)
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
            if (destination.id != R.id.productDataFragment ||
                    destination.id != R.id.registerBusiness){
                binding.addFav.setImageResource(R.drawable.ic_baseline_add_24)
                binding.addFav.setOnClickListener { onParentFabClicked() }
            }
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
                    setOnClickListener {
                        navController.navigate(R.id.clientFragment)
                        onParentFabClicked()
                    }
                }
                addProductFab.apply {
                    visible()
                    setOnClickListener {
                        navController.navigate(R.id.productDataFragment)
                        onParentFabClicked()
                    }
                }
                addSuplierFab.apply {
                    visible()
                    setOnClickListener {
                        navController.navigate(R.id.supplierFragment)
                        onParentFabClicked()
                    }
                }
            }else{
                addClientFab.invisible()
                addProductFab.invisible()
                addSuplierFab.invisible()
            }
        }
    }

    private fun setAnimation(clicked: Boolean){
        binding.apply {
            if (!clicked){
                addFav.startAnimation(rotateOpen)
                addClientFab.startAnimation(fromBottom)
                addProductFab.startAnimation(fromBottom)
                addSuplierFab.startAnimation(fromBottom)
            }else{
                addFav.startAnimation(rotateClose)
                addClientFab.startAnimation(toBottom)
                addProductFab.startAnimation(toBottom)
                addSuplierFab.startAnimation(toBottom)
            }
        }
    }
    private fun setClickable(clicked: Boolean){
        binding.apply {
            if (!clicked){
                addClientFab.isClickable = true
                addProductFab.isClickable = true
                addSuplierFab.isClickable = true
            }else{
                addClientFab.isClickable = false
                addProductFab.isClickable = false
                addSuplierFab.isClickable = false
            }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}