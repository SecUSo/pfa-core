package org.secuso.pfacore.ui.permission

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.secuso.pfacore.ui.PFAPermissionAcquirer
import org.secuso.pfacore.ui.PFAPermissionDSL
import org.secuso.pfacore.R
import org.secuso.pfacore.model.permission.Camera
import org.secuso.pfacore.model.permission.PFAPermission
import org.secuso.pfacore.model.permission.PFAPermissionOwner
import org.secuso.pfacore.ui.PFAPermissionLauncher
import org.secuso.pfacore.ui.asFunction
import org.secuso.pfacore.ui.asLauncher
import org.secuso.pfacore.ui.declareUsage

class CameraUsageProvider<A>(private val owner: A, val default: Boolean = true)
where
    A: AppCompatActivity,
    A: PFAPermissionOwner
{

    fun takePictureImmediately(
        bitmapConsumer: (Bitmap) -> Unit,
        onError: (() -> Unit)? = null
    ): PFAPermissionLauncher {

        val onError = onError ?: {
            Log.d("PFA-Core-Camera", "Could not take picture")
            Unit
        }
        return Camera(owner) { action ->
            PFAPermission.Camera.declareUsage(owner) {
                if (default) {
                    DEFAULT_RATIONALE(owner)
                }
                onGranted = {
                    action()
                }
            }.asFunction()
        }.takePictureImmediately(bitmapConsumer, onError).asLauncher()
    }


    companion object {
        fun PFAPermissionAcquirer.Builder.DEFAULT_RATIONALE(context: Context) {
            showRationale = {
                rationaleText = { ContextCompat.getString(it, R.string.camera_default_rationale_title) }
                rationaleTitle = { ContextCompat.getString(it, R.string.camera_default_rationale_description) }
            }
        }
    }
}