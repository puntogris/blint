package com.puntogris.blint.common.presentation.main

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.snackbar.Snackbar
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.getNavController
import com.puntogris.blint.common.utils.getNavHostFragment
import com.puntogris.blint.common.utils.hideKeyboard
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.ActivityMainBinding
import com.puntogris.blint.feature_store.presentation.main.MainViewModel
import com.puntogris.blint.feature_store.presentation.main.SetupUiListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity :
    AppCompatActivity(),
    NavController.OnDestinationChangedListener,
    NavigationBarView.OnItemSelectedListener,
    SetupUiListener {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Blint_Theme_DayNight)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUpNavigation()
        setUpScanner()
    }

    private fun setUpScanner() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    navController.navigate(R.id.scannerFragment)
                } else {
                    showSnackBar(getString(R.string.snack_require_camera_permission))
                }
            }
    }

    private fun setUpNavigation() {
        navController = getNavController()
        navController.graph = navController.navInflater.inflate(R.navigation.navigation).apply {
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
     * We call setHomeAsUpIndicator here to change the back icon.
     * There seems to not be a better way for this currently.
     * https://issuetracker.google.com/u/1/issues/121078028
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

    override fun onBackPressed() {
        if (isTaskRoot &&
            getNavHostFragment().childFragmentManager.backStackEntryCount == 0 &&
            supportFragmentManager.backStackEntryCount == 0
        ) {
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
