package org.secuso.pfacore.ui.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import org.secuso.pfacore.model.dialog.AbortElseDialog
import org.secuso.pfacore.model.permission.PFAPermission
import org.secuso.pfacore.model.permission.PFAPermissionRequestHandler
import org.secuso.pfacore.ui.view.dialog.show

class PFAPermissionAcquirer(
    internal val activity: AppCompatActivity,
    val handler: PFAPermissionRequestHandler
) {

    fun request(permission: PFAPermission, activator:(() -> Unit) -> Inflatable): Inflatable = activator { permission.request(activity, handler) }
    fun request(permission: PFAPermission): () -> Unit = { permission.request(activity, handler) }

    class Builder(val activity: AppCompatActivity) {
        lateinit var onGranted: () -> Unit
        var onDenied: () -> Unit = { }
        var finally: () -> Unit = { }
        lateinit var showRationale: RationaleOrDialog.() -> Unit

        internal fun build(): PFAPermissionAcquirer {
            val handler = PFAPermissionRequestHandler( onGranted, onDenied, finally, RationaleOrDialog().apply(showRationale).rationale(activity))
            return PFAPermissionAcquirer(activity, handler)
        }


        class RationaleOrDialog {
            var rationaleTitle: String? = null
            var rationaleText: String? = null
            var rationale: (Context) -> (doRequest: () -> Unit) -> Unit = { ctx ->
                { doRequest ->
                    AbortElseDialog.build {
                        title = { rationaleTitle ?: throw IllegalStateException("Either specify a custom rationale or specify a title for the default rationale.") }
                        content = { rationaleText ?: throw IllegalStateException("Either specify a custom rationale or specify a content for the default rationale.") }
                        acceptLabel = ctx.getString(android.R.string.ok)
                        context = ctx
                        onElse = { doRequest() }
                    }.show()
                }
            }
        }
    }

    companion object {
        fun build(activity: AppCompatActivity, initializer: Builder.() -> Unit) = Builder(activity).apply(initializer).build()
    }
}

fun PFAPermission.declareUsage(activity: AppCompatActivity, activator: (() -> Unit) -> Inflatable, initializer: PFAPermissionAcquirer.Builder.() -> Unit) =
    this.declareUsage(activator, PFAPermissionAcquirer.build(activity, initializer))
fun PFAPermission.declareUsage(activator: (() -> Unit) -> Inflatable, requester: PFAPermissionAcquirer): Inflatable {
    this.initiatePermissionRequestLauncher(requester.activity, requester.handler)
    return requester.request(this, activator)
}

fun List<PFAPermission>.declareUsage(activator: (() -> Unit) -> Inflatable, requester: PFAPermissionAcquirer): Inflatable {
    return activator { this.declareUsage(requester) }
}

fun PFAPermission.declareUsage(activity: AppCompatActivity, initializer: PFAPermissionAcquirer.Builder.() -> Unit) =
    this.declareUsage(PFAPermissionAcquirer.build(activity, initializer))
fun PFAPermission.declareUsage(requester: PFAPermissionAcquirer): () -> Unit {
    this.initiatePermissionRequestLauncher(requester.activity, requester.handler)
    return requester.request(this)
}

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
                    if (permissionStatus.any { !it.second }) {
                        requester.handler.onDenied()
                    } else {
                        requester.handler.onGranted()
                    }
                    requester.handler.finally()
                }
            }
            showRationale = {
                rationale = {
                    {
                        if (!rationaleShown) {
                            requester.handler.showRationale(it)
                        } else {
                            it()
                        }
                    }
                }
            }
        }
        it.initiatePermissionRequestLauncher(acquirer.activity, acquirer.handler)
        acquirer.request(it)
    }
    return {
        permissionAcquirers.forEach { it() }
    }
}
