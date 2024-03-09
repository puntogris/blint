package com.puntogris.blint.common.presentation.main

import android.Manifest
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseActivity
import com.puntogris.blint.common.utils.getNavController
import com.puntogris.blint.common.utils.hideKeyboard
import com.puntogris.blint.databinding.ActivityMainBinding
import com.puntogris.blint.feature_store.presentation.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun preInitViews() {
        setTheme(R.style.Blint_Theme_DayNight)
    }

    override fun initializeViews() {
        setUpNavigation()
        setUpScanner()
    }

    private fun setUpScanner() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) navController.navigate(R.id.scannerFragment)
                else showSnackBar(getString(R.string.snack_require_camera_permission))
            }
    }

    private fun setUpNavigation() {
        navController = getNavController()
        navController.graph = navController.navInflater.inflate(R.navigation.navigation)
            .apply {
                setStartDestination(
                    when {
                        viewModel.showLoginScreen() -> R.id.loginFragment
                        viewModel.showNewUserScreen() -> R.id.newUserFragment
                        else -> R.id.homeFragment
                    }
                )
            }

        navController.addOnDestinationChangedListener(this@MainActivity)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.loginFragment,
                R.id.manageTradersFragment,
                R.id.manageProductsFragment,
                R.id.syncAccountFragment,
                R.id.newUserFragment,
                R.id.publishOrderFragment
            )
        )

        binding.bottomAppBar.apply {
            setupWithNavController(navController)
            setOnItemSelectedListener(this@MainActivity)
        }

        navController.addOnDestinationChangedListener(this@MainActivity)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /*
   We call setHomeAsUpIndicator here to change the back icon.
   There seems to not be a better way for this currently.
   https://issuetracker.google.com/u/1/issues/121078028
    */
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        val destinationsWithToolbar = listOf(
            R.id.homeFragment,
            R.id.manageProductsFragment,
            R.id.manageOrdersFragment
        )
        binding.bottomAppBar.isVisible = destination.id in destinationsWithToolbar

        hideKeyboard()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> navController.navigate(R.id.homeFragment)
            R.id.menu_products -> navController.navigate(R.id.manageProductsFragment)
            R.id.menu_orders -> navController.navigate(R.id.manageOrdersFragment)
            R.id.menu_settings -> navController.navigate(R.id.preferencesFragment)
            R.id.menu_search -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        return true
    }

    override fun showSnackBar(
        message: String,
        duration: Int,
        actionText: Int,
        action: View.OnClickListener?
    ) {
        Snackbar.make(binding.root, message, duration).apply {
            anchorView = when {
                binding.bottomAppBar.isVisible -> binding.bottomAppBar
                else -> null
            }
            if (action != null) setAction(actionText, action)
        }.show()
    }
}
