package com.puntogris.blint.ui.main

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.*
import com.puntogris.blint.R
import com.puntogris.blint.databinding.ActivityMainBinding
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.ui.base.BaseActivity
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var clicked = false
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    @Inject
    lateinit var sharedPref: SharedPref

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom_anim
        )
    }

    private val viewModel: MainViewModel by viewModels()

    override fun preInitViews() {
        setTheme(R.style.Theme_Blint)
    }

    override fun initializeViews() {
        setUpTheme()
        setUpNavigation()
        setUpScanner()
    }

    private fun setUpTheme() {
        AppCompatDelegate.setDefaultNightMode(sharedPref.getThemePref())
    }

    private fun setUpScanner() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) navController.navigate(R.id.scannerFragment)
                else showLongSnackBarAboveFab("Necesitamos acceso a la camara para poder abrir el escaner.")
            }
    }

    private fun setUpNavigation() {
        setSupportActionBar(binding.toolbar)
        navController = getNavController()
        navController.graph = navController.navInflater.inflate(R.navigation.navigation)
            .apply {
                startDestination =
                    if (viewModel.isUserLoggedIn()) {
                        if (sharedPref.getUserHasBusinessRegisteredPref()) R.id.mainFragment
                        else R.id.welcomeNewUserFragment
                    } else R.id.loginFragment
            }

        binding.run {
            navController.addOnDestinationChangedListener(this@MainActivity)
        }
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.mainFragment,
                R.id.registerBusinessFragment,
                R.id.preferencesFragment,
                R.id.loginFragment,
                R.id.welcomeNewUserFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.bottomAppBar.apply {
            setNavigationOnClickListener {
                if (navController.currentDestination?.id != R.id.mainFragment)
                    navController.navigate(R.id.mainFragment)
                if (clicked) onParentFabClicked()
            }
            setOnMenuItemClickListener(this@MainActivity)
        }
    }

    private fun onParentFabClicked() {
        setAnimation(clicked)
        setVisibility(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        binding.apply {
            if (!clicked) {
                addClientFab.apply {
                    visible()
                    setOnClickListener { navController.navigate(R.id.editClientFragment) }
                }
                addProductFab.apply {
                    visible()
                    setOnClickListener { navController.navigate(R.id.editProductFragment) }
                }
                addSupplierFab.apply {
                    visible()
                    setOnClickListener { navController.navigate(R.id.editSupplierFragment) }
                }
            } else listOf(addClientFab, addProductFab, addSupplierFab).makeInvisible()
        }
    }

    private fun setAnimation(clicked: Boolean) {
        binding.apply {
            if (!clicked) {
                addFav.startAnimation(rotateOpen)
                listOf(addClientFab, addProductFab, addSupplierFab).setGroupAnimation(fromBottom)
            } else {
                addFav.startAnimation(rotateClose)
                listOf(addClientFab, addProductFab, addSupplierFab).setGroupAnimation(toBottom)
            }
        }
    }

    private fun setClickable(clicked: Boolean) {
        binding.apply {
            if (!clicked) listOf(
                addClientFab,
                addProductFab,
                addSupplierFab
            ).setGroupClickable(true)
            else listOf(addClientFab, addProductFab, addSupplierFab).setGroupClickable(false)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (
            destination.id == R.id.loginFragment ||
            destination.id == R.id.welcomeNewUserFragment ||
            destination.id == R.id.registerBusinessFragment ||
            destination.id == R.id.registerLocalBusinessFragment ||
            destination.id == R.id.registerOnlineBusinessFragment ||
            destination.id == R.id.loginProblemsFragment ||
            destination.id == R.id.businessWaitingRoomFragment ||
            destination.id == R.id.joinBusinessFragment
        ) {
            binding.addFav.isClickable = false
            binding.bottomAppBar.gone()
            binding.addFav.hide()
        } else if (
            destination.id == R.id.aboutFragment ||
            destination.id == R.id.termsConditionsFragment ||
            destination.id == R.id.privacyPolicyFragment ||
            destination.id == R.id.createBackupFragment ||
            destination.id == R.id.restoreBackupFragment
        ) {
            binding.bottomAppBar.performHide()
            binding.addFav.hide()
        } else {
            binding.bottomAppBar.visible()
            binding.addFav.show()
            binding.bottomAppBar.performShow()
        }
        binding.addFav.changeIconFromDrawable(R.drawable.ic_baseline_add_24)
        binding.addFav.setOnClickListener { onParentFabClicked() }
        if (clicked) onParentFabClicked()
    }

    override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
        when (menuItem?.itemId) {
            R.id.scannerFragment -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            R.id.preferencesFragment -> navController.navigate(R.id.preferencesFragment)
        }
        return true
    }
}