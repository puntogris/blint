package com.puntogris.blint.ui.settings

import com.puntogris.blint.R
import com.puntogris.blint.ui.base.BasePreferences
import com.puntogris.blint.utils.registerUiInterface

class NotificationsPreferences: BasePreferences(R.xml.notifications_preferences) {

    override fun initializeViews() {
        registerUiInterface.register()

    }
}