package org.secuso.pfacore.model

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import org.secuso.pfacore.ui.AboutData
import org.secuso.pfacore.ui.help.HelpData
import org.secuso.pfacore.ui.settings.ISettings
import org.secuso.privacyfriendlybackup.api.pfa.BackupManager.backupCreator
import org.secuso.privacyfriendlybackup.api.pfa.BackupManager.backupRestorer
import org.secuso.pfacore.backup.BackupCreator
import org.secuso.pfacore.backup.BackupRestorer

abstract class PFApplication : Application(), Configuration.Provider {
    abstract val About: AboutData
    abstract val Help: HelpData
    abstract val Settings: ISettings
    abstract val ApplicationName: String
    abstract val LightMode: Boolean
    abstract val Database: PFDatabase
    val Backup = object : PFAppBackup {}

    override fun onCreate() {
        super.onCreate()
        _instance = this
        backupCreator = BackupCreator()
        backupRestorer = BackupRestorer()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setMinimumLoggingLevel(Log.INFO).build()
    }

    companion object {
        private var _instance: PFApplication? = null
        val instance
            get() = _instance ?: throw IllegalStateException("The PFApplication was not instanced")
    }
}