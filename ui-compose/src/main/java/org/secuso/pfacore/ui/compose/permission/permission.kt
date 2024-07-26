package org.secuso.pfacore.ui.compose.permission

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.secuso.pfacore.model.dialog.AbortElseDialog
import org.secuso.pfacore.model.permission.PFAPermission
import org.secuso.pfacore.model.permission.PFAPermissionRequestHandler

class PFAPermissionRequester(
    private val activity: AppCompatActivity,
    val onGranted: @Composable () -> Unit,
    val onDenied: @Composable () -> Unit,
    val onRationale: @Composable (doRequest: () -> Unit) -> Unit = { it() }
) {
    @SuppressLint("ComposableNaming")
    @Composable
    fun request(permission: PFAPermission) {
        val doGranted = remember {
            mutableStateOf(false)
        }
        val doDenied = remember {
            mutableStateOf(false)
        }
        val doRationale = remember {
            mutableStateOf<(() -> Unit)?>(null)
        }

        val handler = PFAPermissionRequestHandler(
            onGranted = { doGranted.value = true },
            onDenied = { doDenied.value = true },
            rationale = { }
        )

        if (doGranted.value) {
            onGranted()
        }
        if (doDenied.value) {
            onDenied()
        }
        if (doRationale.value != null) {
            onRationale(doRationale.value!!)
        }

        permission.request(activity, handler)
    }

    class Builder(val activity: AppCompatActivity) {
        lateinit var onGranted: @Composable () -> Unit
        var onDenied: (@Composable () -> Unit)? = null
        lateinit var rationale: RationaleOrDialog.() -> Unit

        internal fun build() = PFAPermissionRequester(activity, onGranted, onDenied ?: {}, RationaleOrDialog().apply(rationale).rationale(activity))

        class RationaleOrDialog {
            var rationaleTitle: String? = null
            var rationaleText: String? = null
            var rationale: (Context) -> @Composable (doRequest: () -> Unit) -> Unit = { ctx ->
                { doRequest ->
                    AbortElseDialog.build {
                        title = { rationaleTitle ?: throw IllegalStateException("Either specify a custom rationale or specify a title for the default rationale.") }
                        content = { rationaleText ?: throw IllegalStateException("Either specify a custom rationale or specify a content for the default rationale.") }
                        context = ctx
                        onElse = { doRequest() }
                    }
                }
            }
        }
    }

    companion object {
        fun build(activity: AppCompatActivity, initializer: Builder.() -> Unit) = Builder(activity).apply(initializer).build()
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun PFAPermission.acquireOrElse(activity: AppCompatActivity, initializer: PFAPermissionRequester.Builder.() -> Unit) =
    this.acquireOrElse(PFAPermissionRequester.build(activity, initializer))

@SuppressLint("ComposableNaming")
@Composable
fun PFAPermission.acquireOrElse(requester: PFAPermissionRequester) = requester.request(this)
