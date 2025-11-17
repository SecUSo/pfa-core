package org.secuso.pfacore.model.permission

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService

interface PFAPermissionOwner {
    fun registerPFAPermissionInitialization(action: () -> Unit)
}

interface PFAPermissionLifecycleCallbacks: Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}

/**
 * Define how a requested permission should be handled.
 * The Android OS decides that an explaining rationale should be shown (e.g. dialog), therefore it is best practice to already explain the usage using [showRationale].
 *
 * @param onGranted Executed if the permission is granted by the system and the user.
 * @param onDenied Executed if the permission is denied.
 * @param finally Executed at the end of the permission request regardless of the outcome.
 * @param showRationale Executed if the system decides that a rationale should be shown.
 *
 * @author Patrick Schneider
 */
class PFAPermissionRequestHandler(
    val onGranted: () -> Unit,
    val onDenied: () -> Unit,
    val finally: () -> Unit = {},
    val showRationale: (doRequest: () -> Unit) -> Unit = { it() }
) {
    class Builder(val activity: Activity)
    {
        lateinit var onGranted: () -> Unit
        var onDenied: () -> Unit = {}
        var finally: () -> Unit = {}
        var showRationale: (doRequest: () -> Unit) -> Unit = { it() }

        internal fun build() = PFAPermissionRequestHandler(onGranted, onDenied, finally, showRationale)
    }

    companion object {
        fun build(activity: Activity, initializer: Builder.() -> Unit) = Builder(activity).apply(initializer).build()
    }
}

/**
 * This class handles the default request process to obtain and use permissions.
 * It is intended to be used declarative and _**must**_ be created in the [onCreate][Activity.onCreate] method due to a lifecycle-observer being used.
 * Use `PFAPermission.*` to see all implemented permissions.
 * The usage of the permission is defined by the `ui-*` libraries as it depends on the ui technology used. However, the intended API and usage is as follows
 *
 * Intended usage:
 *
 *      override fun onCreate(savedInstanceState: Bundle?) {
 *          ...
 *          val requestPermission = PFAPermission.AccessCoarseLocation.declareUsage(this) {
 *              onGranted = {
 *                  Log.d("TestPermission", "permission should be granted: ${ContextCompat.checkSelfPermission(activity, PFAPermission.ScheduleExactAlarm.permission)}")
 *              }
 *              onDenied = {
 *                  Log.d("TestPermission", "permission should be denied: ${ContextCompat.checkSelfPermission(activity, PFAPermission.ScheduleExactAlarm.permission)}")
 *              }
 *              showRationale = {
 *                  rationaleTitle = "This requires the schedule exact alarm permission"
 *                  rationaleText = "Definitely needed."
 *              }
 *          }
 *          findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { requestPermission() }
 *          ...
 *      }
 *
 *  @author Patrick Schneider
 *  @see PFAPermissionRequestHandler
 */
sealed class PFAPermission(
    val sinceAPI: Int,
    val permission: String,
) {
    open fun isGranted(activity: Activity): Boolean = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    open fun showRationale(activity: Activity): Boolean = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)

    protected lateinit var launchPermissionRequest: () -> Unit
    open fun buildPermissionRequestLauncher(activity: AppCompatActivity, handler: PFAPermissionRequestHandler): () -> Unit {
        val launcher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                handler.onGranted()
            } else {
                handler.onDenied()
            }
            handler.finally()
        }
        return { launcher.launch(permission) }
    }
    fun initiatePermissionRequestLauncher(activity: AppCompatActivity, handler: PFAPermissionRequestHandler) {
        launchPermissionRequest = buildPermissionRequestLauncher(activity, handler)
    }
    fun request(activity: AppCompatActivity, handler: PFAPermissionRequestHandler) {
        if (Build.VERSION.SDK_INT < sinceAPI || isGranted(activity)) {
            return handler.onGranted()
        }
        if (!showRationale(activity)) {
            return launchPermissionRequest()
        }
        handler.showRationale { launchPermissionRequest() }
    }

    data object AccessCoarseLocation: PFAPermission(sinceAPI = Build.VERSION_CODES.BASE, permission = Manifest.permission.ACCESS_COARSE_LOCATION)
    data object AccessFineLocation: PFAPermission(sinceAPI = Build.VERSION_CODES.BASE, permission = Manifest.permission.ACCESS_FINE_LOCATION)
    @SuppressLint("InlinedApi")
    data object ActivityRecognition: PFAPermission(sinceAPI = Build.VERSION_CODES.Q, permission = Manifest.permission.ACTIVITY_RECOGNITION)
    data object Camera: PFAPermission(sinceAPI = Build.VERSION_CODES.BASE, permission = Manifest.permission.CAMERA)
    @SuppressLint("InlinedApi")
    data object PostNotifications: PFAPermission(sinceAPI = Build.VERSION_CODES.TIRAMISU, permission = Manifest.permission.POST_NOTIFICATIONS)
    @SuppressLint("InlinedApi")
    data object ScheduleExactAlarm: PFAPermission(sinceAPI = Build.VERSION_CODES.S, permission = Manifest.permission.SCHEDULE_EXACT_ALARM) {
        override fun isGranted(activity: Activity) = activity.getSystemService<AlarmManager>()?.canScheduleExactAlarms() ?: false
        override fun buildPermissionRequestLauncher(activity: AppCompatActivity, handler: PFAPermissionRequestHandler): () -> Unit {
            val alarmManager = activity.getSystemService<AlarmManager>()!!
            activity.registerActivityLifecycleCallbacks(object : PFAPermissionLifecycleCallbacks {
                override fun onActivityResumed(activity: Activity) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        handler.onGranted()
                    } else {
                        handler.onDenied()
                    }
                }
            })
            return { activity.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)) }
        }
    }
    data object RecordAudio: PFAPermission(sinceAPI = Build.VERSION_CODES.BASE, permission = Manifest.permission.RECORD_AUDIO)
    data object WriteExternalStorage: PFAPermission(sinceAPI = Build.VERSION_CODES.DONUT, permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    data object ReadContacts: PFAPermission(sinceAPI = Build.VERSION_CODES.BASE, permission = Manifest.permission.READ_CONTACTS)
}