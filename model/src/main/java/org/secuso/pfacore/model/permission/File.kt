package org.secuso.pfacore.model.permission

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.io.InputStream
import java.io.OutputStream

data class FileSaveOptions(
    val name: String,
    val mimeType: String
)

data class FileLoadOptions(
    val mimeType: String? = null
)

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun <A> A.loadFileFromUserChosenPlace(
    load: (InputStream) -> Unit
): (FileLoadOptions) -> Unit
        where
        A: AppCompatActivity,
        A: LifecycleOwner,
        A: PFAPermissionOwner
{
    var launcher: ActivityResultLauncher<Intent>? = null
    this.registerPFAPermissionInitialization {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    contentResolver.openInputStream(uri)?.use {
                        load(it)
                    }
                }
            }
        }
    }
    return {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = it.mimeType ?: "*/*"
        launcher!!.launch(intent)
    }
}

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun <A> A.saveToUserChosenPlace(
    save: (OutputStream) -> Unit
): (FileSaveOptions) -> Unit
where
    A: AppCompatActivity,
    A: LifecycleOwner,
    A: PFAPermissionOwner
{
    var launcher: ActivityResultLauncher<Intent>? = null
    this.registerPFAPermissionInitialization {
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    contentResolver.openOutputStream(uri)?.use {
                        save(it)
                    }
                }
            }
        }
    }
    return {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_TITLE, it.name)
        intent.type = it.mimeType
        launcher!!.launch(intent)
    }
}
