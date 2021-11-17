package com.puntogris.blint.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.XmlRes
import androidx.preference.PreferenceFragmentCompat
import com.puntogris.blint.utils.UiInterface

abstract class BasePreferences(@XmlRes val layout: Int) : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(layout, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        UiInterface.registerUi()
        initializeViews()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    open fun initializeViews() {}

}