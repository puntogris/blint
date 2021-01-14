package com.puntogris.blint.ui.custom_views

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MonthlyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(
    fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getItem(position: Int): Fragment {
        return MonthlyFragment.newInstance(position)
    }

    override fun getCount(): Int {
        return 12
    }
}