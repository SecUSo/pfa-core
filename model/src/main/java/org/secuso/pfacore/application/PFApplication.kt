package org.secuso.pfacore.application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.multidex.MultiDex
import androidx.room.RoomDatabase
import androidx.work.Configuration
import org.secuso.pfacore.backup.BackupCreator
import org.secuso.pfacore.backup.BackupRestorer
import org.secuso.pfacore.model.ErrorReport
import org.secuso.pfacore.model.ErrorReportHandler
import org.secuso.privacyfriendlybackup.api.pfa.BackupManager.backupCreator
import org.secuso.privacyfriendlybackup.api.pfa.BackupManager.backupRestorer
import java.io.File

abstract class PFApplication : Application(), Configuration.Provider {
    abstract val name: String
    abstract val data: PFData
    abstract val databaseName: String
    abstract val database: Class<out RoomDatabase>
    abstract val mainActivity: Class<out Activity>
    val backup = object : PFAppBackup {}
    private lateinit var errors: File

    override fun onCreate() {
        super.onCreate()
        _instance = this
        backupCreator = BackupCreator()
        backupRestorer = BackupRestorer()
        errors = File(applicationContext.filesDir.path + "/errors")
        errors.mkdirs()

        errors.listFiles()?.forEach {
            if (it.lastModified() - System.currentTimeMillis() > ERROR_REPORT_DELETE_TIME) {
                it.delete()
            }
        }

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            File( "${errors.path}/${System.currentTimeMillis()}").writeText(e.stackTraceToString())
            defaultHandler?.uncaughtException(t, e)
        }
    }

    fun getErrorReports() = errors.listFiles()?.map {
        ErrorReportHandler(
            report = ErrorReport(it.lastModified(), it.readText()),
            deleteReport = { report -> File("${errors.path}/${report.unixTime}") },
            sendReport = { report -> sendEmailErrorReport(report) }
        )
    } ?: listOf()

    fun sendEmailErrorReport(errorReport: ErrorReport) = sendEmailErrorReport(listOf(errorReport))
    fun sendEmailErrorReport(errorReports: List<ErrorReport>) {
        val reports = errorReports.joinToString(
            separator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                System.lineSeparator()
            } else {
                "\n"
            }
        ) {
            File("${errors.path}/${it.unixTime}").readText()
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
        intent.putExtra(Intent.EXTRA_EMAIL, "pfa@secuso.org")
        intent.putExtra(Intent.EXTRA_SUBJECT, "ErrorReport:")
        intent.putExtra(Intent.EXTRA_TEXT, reports)
        startActivity(intent)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setMinimumLoggingLevel(Log.INFO).build()
    }

    companion object {
        private val ERROR_REPORT_DELETE_TIME: Long = 7 * 24 * 60 * 60 * 1000L
        private var _instance: PFApplication? = null
        val instance
            get() = _instance ?: throw IllegalStateException("The PFApplication was not instanced")
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}