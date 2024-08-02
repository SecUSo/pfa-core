package org.secuso.pfacore.ui.compose

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.secuso.pfacore.model.dialog.AbortElseDialog
import org.secuso.pfacore.model.permission.PFAPermission
import org.secuso.pfacore.ui.compose.dialog.Show
import org.secuso.pfacore.model.permission.PFAPermissionRequestHandler as MPFAPermissionRequestHandler

class PFAPermissionRequestHandler(
    val onGranted: @Composable () -> Unit,
    val onDenied: @Composable () -> Unit,
    val finally: @Composable () -> Unit,
    val showRationale: @Composable (doRequest: () -> Unit) -> Unit = { it() }
)

class PFAPermissionAcquirer(
    internal val activity: AppCompatActivity,
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
        val doFinally = remember {
            mutableStateOf(false)
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
        if (doFinally.value) {
            handler.finally()
        }

        return MPFAPermissionRequestHandler(
            onGranted = { doGranted.value = true },
            onDenied = { doDenied.value = true },
            finally = { doFinally.value = true },
            showRationale = { doRationale.value = it }
        )
    }


    @Composable
    fun request(permission: PFAPermission, activator: @Composable (() -> Unit) -> Unit) = setup().apply {
        activator { permission.request(activity, this) }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun request(permission: PFAPermission): Pair<MPFAPermissionRequestHandler, () -> Unit> {
        val handler = setup()
        return handler to { permission.request(activity, handler) }
    }

    class Builder(val activity: AppCompatActivity) {
        lateinit var onGranted: @Composable () -> Unit
        var onDenied: @Composable () -> Unit = { }
        var finally: @Composable () -> Unit = { }
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
                        acceptLabel = ctx.getString(android.R.string.ok)
                        context = ctx
                        onElse = { doRequest() }
                    }.Show()
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
fun List<PFAPermission>.declareUsage(activator: @Composable (() -> Unit) -> Unit, requester: PFAPermissionAcquirer) {
    activator(this.declareUsage(requester))
}

@SuppressLint("ComposableNaming")
@Composable
fun PFAPermission.declareUsage(activity: AppCompatActivity, initializer: PFAPermissionAcquirer.Builder.() -> Unit) =
    this.declareUsage(PFAPermissionAcquirer.build(activity, initializer))

@SuppressLint("ComposableNaming")
@Composable
fun PFAPermission.declareUsage(requester: PFAPermissionAcquirer) = requester.request(this).second

@SuppressLint("ComposableNaming")
@Composable
fun List<PFAPermission>.declareUsage(requester: PFAPermissionAcquirer): () -> Unit {
    val permissionStatus = mutableListOf<Pair<PFAPermission, Boolean>>()
    val permissions = this
    val rationaleShown = false

    val permissionAcquirers = this.map {
        val acquirer = PFAPermissionAcquirer.build(requester.activity) {
            onGranted = { permissionStatus.add(it to true) }
            onDenied = { permissionStatus.add(it to false) }
            finally = {
                if (permissionStatus.size == permissions.size) {
                    if (permissionStatus.any { (_,status) -> !status }) {
                        requester.handler.onDenied()
                    } else {
                        requester.handler.onGranted()
                    }
                    requester.handler.finally()
                }
            }
            showRationale = {
                rationale = {
                    { doRequest ->
                        if (!rationaleShown) {
                            requester.handler.showRationale(doRequest)
                        } else {
                            doRequest()
                        }
                    }
                }
            }
        }

        val (handler, launcher) = acquirer.request(it)
        it.initiatePermissionRequestLauncher(acquirer.activity, handler)
        launcher
    }
    return {
        permissionAcquirers.forEach { it() }
    }
}
