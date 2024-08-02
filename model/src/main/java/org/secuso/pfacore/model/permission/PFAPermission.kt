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

sealed class PFAPermission(
    val sinceAPI: Int,
    val permission: String,
) {
    open fun isGranted(activity: Activity): Boolean = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    open fun showRationale(activity: Activity): Boolean = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    open fun doRequest(activity: AppCompatActivity, handler: PFAPermissionRequestHandler) {
         val launcher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
             if (granted) {
                 handler.onGranted()
             } else {
                 handler.onDenied()
             }
             handler.finally()
         }
        launcher.launch(permission)
    }
    fun request(activity: AppCompatActivity, handler: PFAPermissionRequestHandler) {
        if (Build.VERSION.SDK_INT < sinceAPI || isGranted(activity)) {
            return handler.onGranted()
        }
        if (!showRationale(activity)) {
            return doRequest(activity, handler)
        }
        handler.showRationale { doRequest(activity, handler) }
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
    data object RecordAudio: PFAPermission(sinceAPI = Build.VERSION_CODES.BASE, permission = Manifest.permission.RECORD_AUDIO)
    data object WriteExternalStorage: PFAPermission(sinceAPI = Build.VERSION_CODES.DONUT, permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
}

fun PFAPermission.acquireOrElse(activity: AppCompatActivity, initializer: PFAPermissionRequestHandler.Builder.() -> Unit)
    = this.acquireOrElse(activity, PFAPermissionRequestHandler.build(activity, initializer))
fun PFAPermission.acquireOrElse(activity: AppCompatActivity, handler: PFAPermissionRequestHandler) = this.request(activity, handler)
fun List<PFAPermission>.acquireOrElse(activity: AppCompatActivity, initializer: PFAPermissionRequestHandler.Builder.() -> Unit)
        = this.acquireOrElse(activity, PFAPermissionRequestHandler.build(activity, initializer))
fun List<PFAPermission>.acquireOrElse(activity: AppCompatActivity, handler: PFAPermissionRequestHandler) {
    val permissionStatus = mutableListOf<Pair<PFAPermission, Boolean>>()
    val permissions = this
    val rationaleShown = false
    this.forEachIndexed { index, permission -> permission.acquireOrElse(activity) {
        onGranted = { permissionStatus.add(permissions[index] to true) }
        onDenied = { permissionStatus.add(permissions[index] to false) }
        finally = {
            if (permissionStatus.size == permissions.size) {
                if (permissionStatus.any { !it.second }) {
                    handler.onDenied()
                } else {
                    handler.onGranted()
                }
                handler.finally()
            }
        }
        showRationale = {
            if (!rationaleShown) {
                handler.showRationale(it)
            } else {
                it()
            }
        }
    } }
}