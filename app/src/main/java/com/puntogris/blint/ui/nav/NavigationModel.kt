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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.puntogris.blint.R

/**
 * A class which maintains and generates a navigation list to be displayed by [NavigationAdapter].
 */
object NavigationModel {

    const val HOME_ID = 0
    const val PRODUCTS_ID = 1
    const val SUPPLIERS_ID = 2
    const val CLIENTS_ID = 3
    const val RECORDS_ID = 4
    const val REPORTS_ID = 5
    const val CALENDAR_ID = 6
    const val ACCOUNT_ID = 7
    const val NOTIFICATIONS_ID = 8
    const val SETTINGS_ID = 9


    private var navigationMenuItems = mutableListOf(
        NavigationModelItem.NavMenuItem(
            id = HOME_ID,
            icon = R.drawable.ic_twotone_home_24,
            titleRes = R.string.home,
            checked = false,
            navMenu = NavMenu.HOME
        ),
        NavigationModelItem.NavMenuItem(
            id = PRODUCTS_ID,
            icon = R.drawable.ic_twotone_inventory_2_24,
            titleRes = R.string.products,
            checked = false,
            navMenu = NavMenu.PRODUCTS
        ),
        NavigationModelItem.NavMenuItem(
            id = SUPPLIERS_ID,
            icon = R.drawable.ic_twotone_local_shipping_24,
            titleRes = R.string.suppliers,
            checked = false,
            navMenu = NavMenu.SUPPLIERS
        ),
        NavigationModelItem.NavMenuItem(
            id = CLIENTS_ID,
            icon = R.drawable.ic_twotone_people_24,
            titleRes = R.string.clients,
            checked = false,
            navMenu = NavMenu.CLIENTS
        ),
        NavigationModelItem.NavMenuItem(
            id = RECORDS_ID,
            icon = R.drawable.ic_twotone_article_24,
            titleRes = R.string.records,
            checked = false,
            navMenu = NavMenu.ORDERS
        ),
        NavigationModelItem.NavMenuItem(
            id = REPORTS_ID,
            icon = R.drawable.ic_twotone_insert_chart_24,
            titleRes = R.string.reports,
            checked = false,
            navMenu = NavMenu.RECORDS
        ),
        NavigationModelItem.NavMenuItem(
            id = CALENDAR_ID,
            icon = R.drawable.ic_twotone_calendar_today_24,
            titleRes = R.string.calendar,
            checked = false,
            navMenu = NavMenu.EVENTS
        ),
        NavigationModelItem.NavMenuItem(
            id = CALENDAR_ID,
            icon = R.drawable.ic_baseline_grain_24,
            titleRes = R.string.tools,
            checked = false,
            navMenu = NavMenu.TOOLS
        ),
        NavigationModelItem.NavMenuItem(
            id = CALENDAR_ID,
            icon = R.drawable.ic_twotone_monetization_on_24,
            titleRes = R.string.debts,
            checked = false,
            navMenu = NavMenu.DEBTS
        ),NavigationModelItem.NavMenuItem(
            id = CALENDAR_ID,
            icon = R.drawable.ic_twotone_account_circle_24,
            titleRes = R.string.account,
            checked = false,
            navMenu = NavMenu.ACCOUNT
        )
    )

    private val secondaryItems= listOf(
        NavigationModelItem.NavMenuItem(
            id = NOTIFICATIONS_ID,
            icon = R.drawable.ic_baseline_notifications_24,
            titleRes = R.string.notifications,
            checked = false,
            navMenu = NavMenu.NOTIFICATIONS
        ),
        NavigationModelItem.NavMenuItem(
            id = SETTINGS_ID,
            icon = R.drawable.ic_twotone_settings_24,
            titleRes = R.string.settings,
            checked = false,
            navMenu = NavMenu.SETTINGS
        ))

    private val _navigationList: MutableLiveData<List<NavigationModelItem>> = MutableLiveData()
    val navigationList: LiveData<List<NavigationModelItem>>
        get() = _navigationList

    init {
        postListUpdate()
    }

    /**
     * Set the currently selected menu item.
     *
     * @return true if the currently selected item has changed.
     */
    fun setNavigationMenuItemChecked(id: Int): Boolean {
        var updated = false
        navigationMenuItems.forEachIndexed { index, item ->
            val shouldCheck = item.id == id
            if (item.checked != shouldCheck) {
                navigationMenuItems[index] = item.copy(checked = shouldCheck)
                updated = true
            }
        }
        if (updated) postListUpdate()
        return updated
    }

    private fun postListUpdate() {
        val newList = navigationMenuItems +
                    (NavigationModelItem.NavDivider("Configuracion")) + secondaryItems


        _navigationList.value = newList
    }

}

