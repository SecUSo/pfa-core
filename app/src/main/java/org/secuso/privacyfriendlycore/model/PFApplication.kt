package org.secuso.privacyfriendlycore.model

import android.app.Activity
import android.app.Application
import android.util.JsonWriter
import android.util.Log
import androidx.work.Configuration
import org.secuso.privacyfriendlycore.ui.AboutData
import org.secuso.privacyfriendlycore.ui.help.Help
import org.secuso.privacyfriendlycore.ui.settings.ISettings
import org.secuso.privacyfriendlybackup.api.pfa.BackupManager.backupCreator
import org.secuso.privacyfriendlybackup.api.pfa.BackupManager.backupRestorer
import org.secuso.privacyfriendlycore.backup.BackupCreator
import org.secuso.privacyfriendlycore.backup.BackupRestorer

abstract class PFApplication : Application(), Configuration.Provider {
    abstract val About: AboutData
    abstract val Help: Help
    abstract val Settings: ISettings
    abstract val ApplicationName: String
    abstract val LightMode: Boolean
    abstract val Database: PFDatabase
    val Backup = object : PFAppBackup {}

    override fun onCreate() {
        super.onCreate()
        instance = this
        backupCreator = BackupCreator()
        backupRestorer = BackupRestorer()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setMinimumLoggingLevel(Log.INFO).build()
    }

    companion object {
        lateinit var instance: PFApplication
            private set
    }
}