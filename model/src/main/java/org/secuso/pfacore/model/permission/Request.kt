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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService

interface PFAPermissionLifecycleCallbacks: Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}

class PFAPermissionRequestHandler(
    val onGranted: () -> Unit,
    val onDenied: () -> Unit,
    val rationale: (doRequest: () -> Unit) -> Unit = { it() }
)

sealed class PFAPermission(
    val sinceAPI: Int,
    val permission: String,
) {
    open fun isGranted(activity: Activity): Boolean = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    open fun showRationale(activity: Activity): Boolean = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    open fun doRequest(activity: AppCompatActivity, handler: PFAPermissionRequestHandler) {
         activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
             if (granted) {
                 handler.onGranted()
             } else {
                 handler.onDenied()
             }
         }
    }
    fun request(activity: AppCompatActivity, handler: PFAPermissionRequestHandler) {
        if (Build.VERSION.SDK_INT < sinceAPI || isGranted(activity)) {
            return handler.onGranted()
        }
        if (!showRationale(activity)) {
            return doRequest(activity, handler)
        }
        handler.rationale { doRequest(activity, handler) }
    }


    @SuppressLint("InlinedApi")
    data object PostNotifications: PFAPermission(sinceAPI = Build.VERSION_CODES.TIRAMISU, permission = Manifest.permission.POST_NOTIFICATIONS)
    @SuppressLint("InlinedApi")
    data object ScheduleExactAlarm: PFAPermission(sinceAPI = Build.VERSION_CODES.S, permission = Manifest.permission.SCHEDULE_EXACT_ALARM) {
        override fun doRequest(activity: AppCompatActivity, handler: PFAPermissionRequestHandler) {
            val alarmManager = activity.getSystemService<AlarmManager>()!!
            if (alarmManager.canScheduleExactAlarms()) {
                return handler.onGranted()
            }
            activity.registerActivityLifecycleCallbacks(object : PFAPermissionLifecycleCallbacks {
                override fun onActivityResumed(activity: Activity) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        handler.onGranted()
                    } else {
                        handler.onDenied()
                    }
                }
            })
            activity.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
        }
    }

}
