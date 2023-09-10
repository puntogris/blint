package com.puntogris.blint.feature_store.presentation.reports

import com.puntogris.blint.R

enum class TimeFrame(val res: Int, val days: Int) {
    WEEKLY(R.string.weekly, 7),
    MONTHLY(R.string.monthly, 30),
    QUARTERLY(R.string.quarterly, 90),
    BIANNUAL(R.string.biannual, 180),
    ANNUAL(R.string.annual, 360),
    HISTORICAL(R.string.historical, 0)
}
