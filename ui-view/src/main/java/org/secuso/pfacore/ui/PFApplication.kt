package org.secuso.pfacore.ui

import org.secuso.pfacore.application.PFData as MPFData
import org.secuso.pfacore.application.PFModelApplication
import org.secuso.pfacore.model.ErrorReportHandler
import org.secuso.pfacore.model.dialog.AbortElseDialog
import org.secuso.pfacore.ui.help.Help
import org.secuso.pfacore.ui.preferences.settings.Settings
import org.secuso.pfacore.ui.tutorial.Tutorial

typealias PFData = MPFData<Settings, Help, Tutorial>
abstract class PFApplication: PFModelApplication<PFData>() {

    override fun onCrash(error: ErrorReportHandler) {
        AbortElseDialog.build(this) {
            acceptLabel = "Report"
            title = { "A crash happened" }
            content = { error.report.trace }
            onElse = { error.send() }
        }
    }

    companion object {
        val instance
            get() = PFModelApplication.instance as PFApplication
    }
}