package org.secuso.pfacore.model.permission

import android.graphics.Bitmap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner

class Camera<A>(val owner: A, private val useCameraPermissionProvider: (() -> Unit) -> (() -> Unit))
where
    A: AppCompatActivity,
    A: LifecycleOwner,
    A: PFAPermissionOwner
{
    fun takePictureImmediately(
        imageConsumer: (Bitmap) -> Unit,
        onError: () -> Unit
    ): () -> Unit
    {
        var launcher: ActivityResultLauncher<Void?>? = null
        owner.registerPFAPermissionInitialization {
            launcher = owner.registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                if (it != null) {
                    imageConsumer(it)
                } else {
                    onError()
                }
            }
        }
        return useCameraPermissionProvider {
            launcher?.launch(null)
        }
    }
}