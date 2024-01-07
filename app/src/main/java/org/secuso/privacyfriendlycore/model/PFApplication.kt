package org.secuso.privacyfriendlycore.model

import android.app.Activity
import android.app.Application
import org.secuso.privacyfriendlycore.ui.AboutData
import org.secuso.privacyfriendlycore.ui.settings.ISettings


abstract class PFApplication : Application() {
    abstract val About: AboutData
    abstract val Settings: ISettings
    abstract val ApplicationName: String
    abstract val LightMode: Boolean

    companion object {
        fun instance(activity: Activity) = activity.application as PFApplication
    }
}