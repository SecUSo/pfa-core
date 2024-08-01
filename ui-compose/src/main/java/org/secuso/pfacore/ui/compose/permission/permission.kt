package org.secuso.pfacore.ui.compose.permission

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.secuso.pfacore.model.dialog.AbortElseDialog
import org.secuso.pfacore.model.permission.PFAPermission
import org.secuso.pfacore.model.permission.PFAPermissionRequestHandler as MPFAPermissionRequestHandler

class PFAPermissionRequestHandler(
    val onGranted: @Composable () -> Unit,
    val onDenied: @Composable () -> Unit,
    val finally: () -> Unit,
    val showRationale: @Composable (doRequest: () -> Unit) -> Unit = { it() }
)

class PFAPermissionAcquirer(
    private val activity: AppCompatActivity,
    val handler: PFAPermissionRequestHandler
) {
    @Composable
    fun setup(): MPFAPermissionRequestHandler {
        val doGranted = remember {
            mutableStateOf(false)
        }
        val doDenied = remember {
            mutableStateOf(false)
        }
        val doRationale = remember {
            mutableStateOf<(() -> Unit)?>(null)
        }

        if (doGranted.value) {
            handler.onGranted()
        }
        if (doDenied.value) {
            handler.onDenied()
        }
        if (doRationale.value != null) {
            handler.showRationale(doRationale.value!!)
        }

        return MPFAPermissionRequestHandler(
            onGranted = { doGranted.value = true },
            onDenied = { doDenied.value = true },
            finally = handler.finally,
            showRationale = { doRationale.value = it }
        )
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun request(permission: PFAPermission, activator: @Composable (() -> Unit) -> Unit) {
        val handler = setup()
        activator { permission.request(activity, handler) }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun request(permission: PFAPermission): () -> Unit {
        val handler = setup()
        return { permission.request(activity, handler) }
    }

    class Builder(val activity: AppCompatActivity) {
        lateinit var onGranted: @Composable () -> Unit
        var onDenied: @Composable () -> Unit = { }
        var finally: () -> Unit = { }
        lateinit var showRationale: RationaleOrDialog.() -> Unit

        internal fun build(): PFAPermissionAcquirer {
            val handler = PFAPermissionRequestHandler( onGranted, onDenied, finally, RationaleOrDialog().apply(showRationale).rationale(activity))
            return PFAPermissionAcquirer(activity, handler)
        }


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
fun PFAPermission.declareUsage(activity: AppCompatActivity, activator: @Composable (() -> Unit) -> Unit, initializer: PFAPermissionAcquirer.Builder.() -> Unit) =
    this.declareUsage(activator, PFAPermissionAcquirer.build(activity, initializer))

@SuppressLint("ComposableNaming")
@Composable
fun PFAPermission.declareUsage(activator: @Composable (() -> Unit) -> Unit, requester: PFAPermissionAcquirer) = requester.request(this, activator)

@SuppressLint("ComposableNaming")
@Composable
fun PFAPermission.declareUsage(activity: AppCompatActivity, initializer: PFAPermissionAcquirer.Builder.() -> Unit) =
    this.declareUsage(PFAPermissionAcquirer.build(activity, initializer))

@SuppressLint("ComposableNaming")
@Composable
fun PFAPermission.declareUsage(requester: PFAPermissionAcquirer) = requester.request(this)
