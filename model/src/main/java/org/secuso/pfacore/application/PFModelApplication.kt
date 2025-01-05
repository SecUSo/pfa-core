package org.secuso.pfacore.application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.multidex.MultiDex
import androidx.work.Configuration
import org.secuso.pfacore.R
import org.secuso.pfacore.backup.BackupCreator
import org.secuso.pfacore.backup.BackupRestorer
import org.secuso.pfacore.model.ErrorReport
import org.secuso.pfacore.model.ErrorReportHandler
import org.secuso.privacyfriendlybackup.api.pfa.BackupManager.backupCreator
import org.secuso.privacyfriendlybackup.api.pfa.BackupManager.backupRestorer
import java.io.File
import java.io.IOException

/**
 * This is meant to be the entry point of any Privacy Friendly App.
 * It defines and requires the minimum set of data to create the empty-shell for the PFA,
 * such as drawers or functionality like the settings, help or about sections.
 *
 * Using this class also adds a global listener for any unhandled exception and provides functionality to report all exceptions via email.
 *
 * Intended Usage:
 *
 *      class PFExample : PFApplication() {
 *          override val name: String
 *              get() = getString(R.string.app_name)
 *
 *          override val database
 *              get() = RoomDatabaseConfig(baseContext, AppDatabase.DB_NAME, AppDatabase::class.java)
 *          override val data: PFData
 *              get() = PFApplicationData.instance(baseContext).data
 *          override val mainActivity = MainActivity::class.java
 *      }
 *
 * @author Patrick Schneider
 * @see PFData
 * @see BackupDatabaseConfig
 * @see PFAppBackup
 * @param PFD An instance of [PFData] specifying the preferences, settings and data used by the about and help sections.
 */
abstract class PFModelApplication<PFD: PFData<*,*,*>> : Application(), Configuration.Provider {
    abstract val name: String
    abstract val data: PFD
    abstract val mainActivity: Class<out Activity>
    open val database: BackupDatabaseConfig? = null
    open val backup = object : PFAppBackup {}
    private lateinit var errors: File

    override fun onCreate() {
        super.onCreate()
        _instance = this

        if (database != null) {
            backupCreator = BackupCreator()
            backupRestorer = BackupRestorer()
        }

        errors = File(applicationContext.filesDir.path + "/errors")
        errors.mkdirs()

        errors.listFiles()?.forEach {
            if (it.lastModified() - System.currentTimeMillis() > ERROR_REPORT_DELETE_TIME) {
                it.delete()
            }
        }

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            File( "${errors.path}/${System.currentTimeMillis()}").writeText(e.stackTraceToString())
            defaultHandler?.uncaughtException(t, e)
        }
    }

    fun getErrorReports() = errors.listFiles()?.map {
        val time = it.nameWithoutExtension.toLongOrNull()
        if (time != null) {
            return@map         ErrorReportHandler(
                report = ErrorReport(time, it.readText()),
                deleteReport = { report -> File("${errors.path}/${report.unixTime}").delete() },
                sendReport = { report -> sendEmailErrorReport(report) }
            )
        } else {
            null
        }
    }?.filterNotNull()?.sortedByDescending { it.report.unixTime } ?: listOf()

    fun Collection<ErrorReport>.readAndConcat() = this.map {
        try {
            File("${errors.path}/${it.unixTime}").readText()
        } catch (exception: IOException) {
            exception.printStackTrace()
            null
        }
    }.joinToString(SEPARATOR)

    fun sendEmailErrorReport(errorReport: ErrorReport) = sendEmailErrorReport(listOf(errorReport))
    fun sendEmailErrorReport(errorReports: List<ErrorReport>) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(ContextCompat.getString(applicationContext, R.string.error_report_mail)))
            putExtra(Intent.EXTRA_SUBJECT, String.format(getString(R.string.error_report_email_header), this@PFModelApplication.data.about.name, this@PFModelApplication.data.about.version))
            putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.error_report_email_body), getDeviceInformation().joinToString(SEPARATOR), errorReports.readAndConcat()))
        }
        val chooser = Intent.createChooser(intent, "Send mail")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(chooser)
    }


    fun getDeviceInformation(): List<String> = mutableListOf<String>().apply {
        if (!data.includeDeviceDataInReport.value) {
            return@apply
        }
        add("MODEL: ${Build.MODEL}")
        add("MANUFACTURER: ${Build.MANUFACTURER}")
        add("BRAND: ${Build.BRAND}")
        add("ANDROID-VERSION: ${Build.VERSION.RELEASE}")
        add("SDK: ${Build.VERSION.SDK_INT}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            add("BASE-OS: ${Build.VERSION.BASE_OS}")
        }
    }

    companion object {
        private val ERROR_REPORT_DELETE_TIME: Long = 7 * 24 * 60 * 60 * 1000L
        val SEPARATOR = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            System.lineSeparator()
        } else {
            "\n"
        }
        private var _instance: PFModelApplication<*>? = null
        val instance
            get() = _instance ?: throw IllegalStateException("The PFApplication was not instanced")
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}