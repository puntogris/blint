package com.puntogris.blint.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomappbar.BottomAppBar
import com.puntogris.blint.R
import com.puntogris.blint.databinding.ActivityMainBinding
import com.puntogris.blint.utils.getNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
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
                else -> {
                    false
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.scannerFragment){
                binding.bottomAppBar.visibility = View.GONE
                binding.addFav.visibility = View.GONE

            }else if(destination.id == R.id.createProductFragment){
                binding.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
                binding.bottomAppBar.visibility = View.VISIBLE
                binding.addFav.visibility = View.GONE
                binding.addFav.setImageResource(R.drawable.ic_baseline_close_24)
                binding.addFav.setOnClickListener { navController.navigate(R.id.mainFragment) }
            }
            else{
                binding.addFav.setImageResource(R.drawable.ic_baseline_add_24)
                binding.bottomAppBar.visibility = View.VISIBLE
                binding.addFav.visibility = View.VISIBLE
                binding.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
                binding.addFav.setOnClickListener {
                    navController.navigate(R.id.createProductFragment) }
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
}