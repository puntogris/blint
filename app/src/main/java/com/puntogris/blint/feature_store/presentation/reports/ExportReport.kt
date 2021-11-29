package com.puntogris.blint.feature_store.presentation.reports

import android.net.Uri

class ExportReport(
    var uri: Uri? = null,
    var type: ReportType? = null,
    var timeFrame: TimeFrame = TimeFrame.WEEKLY
)