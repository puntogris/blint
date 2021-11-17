/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.puntogris.blint.ui.nav

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.shape.MaterialShapeDrawable
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentBottomNavDrawerBinding
import com.puntogris.blint.model.BusinessItem
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.main.MainViewModel
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.math.abs

@AndroidEntryPoint
class BottomNavDrawerFragment :
    BaseFragment<FragmentBottomNavDrawerBinding>(R.layout.fragment_bottom_nav_drawer),
    NavigationAdapter.NavigationAdapterListener,
    AccountAdapter.AccountAdapterListener {

    enum class SandwichState {
        CLOSED,
        OPEN,
        SETTLING
    }

    @ExperimentalCoroutinesApi
    private val viewModel: MainViewModel by viewModels()

    private val behavior: BottomSheetBehavior<FrameLayout> by lazy(NONE) {
        from(binding.backgroundContainer)
    }

    private val bottomSheetCallback = BottomNavigationDrawerCallback()

    private val sandwichSlideActions = mutableListOf<OnSandwichSlideAction>()

    private val navigationListeners: MutableList<NavigationAdapter.NavigationAdapterListener> =
        mutableListOf()

    private val backgroundShapeDrawable: MaterialShapeDrawable by lazy(NONE) {
        val backgroundContext = binding.backgroundContainer.context
        MaterialShapeDrawable(
            backgroundContext,
            null,
            R.attr.bottomSheetStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(
                backgroundContext.themeColor(R.attr.colorPrimarySurfaceVariant)
            )
            elevation = resources.getDimension(R.dimen.plane_08)
            initializeElevationOverlay(requireContext())
        }
    }

    private val foregroundShapeDrawable: MaterialShapeDrawable by lazy(NONE) {
        val foregroundContext = binding.foregroundContainer.context
        MaterialShapeDrawable(
            foregroundContext,
            null,
            R.attr.bottomSheetStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(
                foregroundContext.themeColor(R.attr.bottomSheetDayNight)
            )
            elevation = resources.getDimension(R.dimen.plane_16)
            shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_NEVER
            initializeElevationOverlay(requireContext())
            shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                .setTopEdge(
                    SemiCircleEdgeCutoutTreatment(
                        resources.getDimension(R.dimen.grid_1),
                        resources.getDimension(R.dimen.grid_3),
                        0F,
                        resources.getDimension(R.dimen.navigation_drawer_profile_image_size_padded)
                    )
                )
                .build()
        }
    }

    private var sandwichState: SandwichState = SandwichState.CLOSED
    private var sandwichAnim: ValueAnimator? = null
    private val sandwichInterp by lazy(NONE) {
        requireContext().themeInterpolator(R.attr.motionInterpolatorPersistent)
    }

    // Progress value which drives the animation of the sandwiching account picker. Responsible
    // for both calling progress updates and state updates.
    private var sandwichProgress: Float = 0F
        set(value) {
            if (field != value) {
                onSandwichProgressChanged(value)
                val newState = when (value) {
                    0F -> SandwichState.CLOSED
                    1F -> SandwichState.OPEN
                    else -> SandwichState.SETTLING
                }
                if (sandwichState != newState) onSandwichStateChanged(newState)
                sandwichState = newState
                field = value
            }
        }

    private val closeDrawerOnBackPressed = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            close()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, closeDrawerOnBackPressed)
    }

    override fun initializeViews() {
        binding.foregroundContainer.setOnApplyWindowInsetsListener { view, windowInsets ->
            // Record the window's top inset so it can be applied when the bottom sheet is slide up
            // to meet the top edge of the screen.
            view.setTag(
                R.id.tag_system_window_inset_top,
                windowInsets.systemWindowInsetTop
            )
            windowInsets
        }
    }


    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            backgroundContainer.background = backgroundShapeDrawable
            foregroundContainer.background = foregroundShapeDrawable

            scrimView.setOnClickListener { close() }

            bottomSheetCallback.apply {
                // Scrim view transforms
                addOnSlideAction(AlphaSlideAction(scrimView))
                addOnStateChangedAction(VisibilityStateAction(scrimView))
                // Foreground transforms
                addOnSlideAction(
                    ForegroundSheetTransformSlideAction(
                        binding.foregroundContainer,
                        foregroundShapeDrawable,
                        binding.profileImageView
                    )
                )
                // Recycler transforms
                addOnStateChangedAction(ScrollToTopStateAction(navRecyclerView))
                // Close the sandwiching account picker if open
                addOnStateChangedAction(object : OnStateChangedAction {
                    override fun onStateChanged(sheet: View, newState: Int) {
                        sandwichAnim?.cancel()
                        sandwichProgress = 0F
                    }
                })
                // If the drawer is open, pressing the system back button should close the drawer.
                addOnStateChangedAction(object : OnStateChangedAction {
                    override fun onStateChanged(sheet: View, newState: Int) {
                        closeDrawerOnBackPressed.isEnabled = newState != STATE_HIDDEN
                    }
                })
            }

            profileImageView.setOnClickListener { toggleSandwich() }

            behavior.addBottomSheetCallback(bottomSheetCallback)
            behavior.state = STATE_HIDDEN

            val adapter = NavigationAdapter(this@BottomNavDrawerFragment)

            navRecyclerView.adapter = adapter
            NavigationModel.navigationList.observe(viewLifecycleOwner, {
                adapter.submitList(it)
            })
            NavigationModel.setNavigationMenuItemChecked(0)

            val accountAdapter = AccountAdapter(this@BottomNavDrawerFragment)
            accountRecyclerView.adapter = accountAdapter

            lifecycleScope.launch {
                viewModel.currentUser.collect {
                    val dataForAdapter = viewModel.getBusinessList().map { employee ->
                        val tempBusiness = BusinessItem(
                            employee.businessId,
                            employee.businessName,
                            employee.businessType,
                            employee.businessOwner,
                            R.drawable.ic_baseline_storefront_24,
                            false,
                            businessStatus = employee.businessStatus
                        )

                        if (tempBusiness.businessId == it.businessId) {
                            tempBusiness.isCurrentAccount = true
                            binding.currentUserAccount = tempBusiness
                        }
                        tempBusiness
                    }
                    accountAdapter.submitList(dataForAdapter)
                }
            }
        }
    }

    fun toggle() {
        when {
            sandwichState == SandwichState.OPEN -> toggleSandwich()
            behavior.state == STATE_HIDDEN -> open()
            behavior.state == STATE_HIDDEN
                    || behavior.state == STATE_HALF_EXPANDED
                    || behavior.state == STATE_EXPANDED
                    || behavior.state == STATE_COLLAPSED -> close()
        }
    }

    fun open() {
        behavior.state = STATE_HALF_EXPANDED
    }

    fun close() {
        behavior.state = STATE_HIDDEN
    }

    fun addOnSlideAction(action: OnSlideAction) {
        bottomSheetCallback.addOnSlideAction(action)
    }

    fun addOnStateChangedAction(action: OnStateChangedAction) {
        bottomSheetCallback.addOnStateChangedAction(action)
    }

    fun addNavigationListener(listener: NavigationAdapter.NavigationAdapterListener) {
        navigationListeners.add(listener)
    }

    /**
     * Add actions to be run when the slide offset (animation progress) or the sandwiching account
     * picker has changed.
     */
    fun addOnSandwichSlideAction(action: OnSandwichSlideAction) {
        sandwichSlideActions.add(action)
    }

    override fun onNavMenuItemClicked(item: NavigationModelItem.NavMenuItem) {
        NavigationModel.setNavigationMenuItemChecked(item.id)
        navigationListeners.forEach { it.onNavMenuItemClicked(item) }
    }

    @ExperimentalCoroutinesApi
    override fun onAccountClicked(employee: BusinessItem) {
        lifecycleScope.launch {
            viewModel.updateCurrentBusiness(employee.businessId)
            findNavController().navigate(R.id.mainFragment)
        }
        toggleSandwich()
    }

    /**
     * Open or close the account picker "sandwich".
     */
    private fun toggleSandwich() {
        val initialProgress = sandwichProgress
        val newProgress = when (sandwichState) {
            SandwichState.CLOSED -> {
                // Store the original top location of the background container so we can animate
                // the delta between its original top position and the top position needed to just
                // show the account picker RecyclerView, and back again.
                binding.backgroundContainer.setTag(
                    R.id.tag_view_top_snapshot,
                    binding.backgroundContainer.top
                )
                1F
            }
            SandwichState.OPEN -> 0F
            SandwichState.SETTLING -> return
        }
        sandwichAnim?.cancel()
        sandwichAnim = ValueAnimator.ofFloat(initialProgress, newProgress).apply {
            addUpdateListener { sandwichProgress = animatedValue as Float }
            interpolator = sandwichInterp
            duration = (abs(newProgress - initialProgress) *
                    resources.getInteger(R.integer.reply_motion_duration_medium)).toLong()
        }
        sandwichAnim?.start()
    }

    /**
     * Called each time the value of [sandwichProgress] changes. [progress] is the state of the
     * sandwiching, with 0F being the default [SandwichState.CLOSED] state and 1F being the
     * [SandwichState.OPEN] state.
     */
    private fun onSandwichProgressChanged(progress: Float) {
        binding.run {
            val navProgress = lerp(0F, 1F, 0F, 0.5F, progress)
            val accProgress = lerp(0F, 1F, 0.5F, 1F, progress)

            foregroundContainer.translationY =
                (binding.foregroundContainer.height * 0.15F) * navProgress
            profileImageView.scaleX = 1F - navProgress
            profileImageView.scaleY = 1F - navProgress
            profileImageView.alpha = 1F - navProgress
            foregroundContainer.alpha = 1F - navProgress
            accountRecyclerView.alpha = accProgress

            foregroundShapeDrawable.interpolation = 1F - navProgress

            // Animate the translationY of the backgroundContainer so just the account picker is
            // peeked above the BottomAppBar.
            backgroundContainer.translationY = progress *
                    ((scrimView.bottom - accountRecyclerView.height
                            - resources.getDimension(R.dimen.bottom_app_bar_height)) -
                            (backgroundContainer.getTag(R.id.tag_view_top_snapshot) as Int))
        }

        // Call any actions which have been registered to run on progress changes.
        sandwichSlideActions.forEach { it.onSlide(progress) }
    }

    /**
     * Called when the [SandwichState] of the sandwiching account picker has changed.
     */
    private fun onSandwichStateChanged(state: SandwichState) {
        // Change visibility/clickability of views which obstruct user interaction with
        // the account list.
        when (state) {
            SandwichState.OPEN -> {
                binding.run {
                    foregroundContainer.gone()
                    profileImageView.isClickable = false
                }
            }
            else -> {
                binding.run {
                    foregroundContainer.visible()
                    profileImageView.isClickable = true
                }
            }
        }
    }
}