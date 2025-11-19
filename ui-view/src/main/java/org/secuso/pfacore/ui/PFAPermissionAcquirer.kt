package org.secuso.pfacore.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import org.secuso.pfacore.model.dialog.AbortElseDialog
import org.secuso.pfacore.model.permission.PFAPermission
import org.secuso.pfacore.model.permission.PFAPermissionOwner
import org.secuso.pfacore.model.permission.PFAPermissionRequestHandler
import org.secuso.pfacore.ui.dialog.show

interface PFAPermissionLauncher {
    operator fun invoke()
}
fun (() -> Unit).asLauncher() = object : PFAPermissionLauncher {
    override fun invoke() {
        this@asLauncher()
    }
}
fun PFAPermissionLauncher.asFunction() = { this() }

typealias PFAPermissionDSL = org.secuso.pfacore.ui.PFAPermissionAcquirer.Builder.() -> Unit
class PFAPermissionAcquirer(
    internal val activity: AppCompatActivity,
    val handler: PFAPermissionRequestHandler
) {

    fun request(permission: PFAPermission, activator:(() -> Unit) -> org.secuso.pfacore.ui.Inflatable): org.secuso.pfacore.ui.Inflatable = activator { permission.request(activity, handler) }
    fun request(permission: PFAPermission): PFAPermissionLauncher = { permission.request(activity, handler) }.asLauncher()

    class Builder(val activity: AppCompatActivity) {
        lateinit var onGranted: () -> Unit
        var onDenied: () -> Unit = { }
        var finally: () -> Unit = { }
        lateinit var showRationale: org.secuso.pfacore.ui.PFAPermissionAcquirer.Builder.RationaleOrDialog.() -> Unit

        internal fun build(): org.secuso.pfacore.ui.PFAPermissionAcquirer {
            val handler = PFAPermissionRequestHandler( onGranted, onDenied, finally, RationaleOrDialog().apply(showRationale).rationale(activity))
            return org.secuso.pfacore.ui.PFAPermissionAcquirer(activity, handler)
        }


        class RationaleOrDialog {
            var rationaleTitle: ((Context) -> String)? = null
            var rationaleText: ((Context) -> String)? = null
            var rationale: (Context) -> (doRequest: () -> Unit) -> Unit = { ctx ->
                { doRequest ->
                    AbortElseDialog.build(ctx) {
                        title = { rationaleTitle?.invoke(ctx) ?: throw IllegalStateException("Either specify a custom rationale or specify a title for the default rationale.") }
                        content = { rationaleText?.invoke(ctx) ?: throw IllegalStateException("Either specify a custom rationale or specify a content for the default rationale.") }
                        acceptLabel = ctx.getString(android.R.string.ok)
                        onElse = { doRequest() }
                    }.show()
                }
            }
        }
    }

    companion object {
        fun build(activity: AppCompatActivity, initializer: PFAPermissionDSL) = org.secuso.pfacore.ui.PFAPermissionAcquirer.Builder(
            activity
        ).apply(initializer).build()
    }
}

fun <A> PFAPermission.declareUsage(activity: A, activator: (() -> Unit) -> org.secuso.pfacore.ui.Inflatable, initializer: PFAPermissionDSL)
    where
        A: AppCompatActivity,
        A: PFAPermissionOwner =
    this.declareUsage(activator, org.secuso.pfacore.ui.PFAPermissionAcquirer.Companion.build(activity, initializer))
fun PFAPermission.declareUsage(activator: (() -> Unit) -> org.secuso.pfacore.ui.Inflatable, requester: org.secuso.pfacore.ui.PFAPermissionAcquirer): org.secuso.pfacore.ui.Inflatable {
    this.initiatePermissionRequestLauncher(requester.activity, requester.handler)
    return requester.request(this, activator)
}

fun List<PFAPermission>.declareUsage(owner: PFAPermissionOwner, activator: (() -> Unit) -> org.secuso.pfacore.ui.Inflatable, requester: org.secuso.pfacore.ui.PFAPermissionAcquirer): org.secuso.pfacore.ui.Inflatable {
    return activator { this.declareUsage(owner, requester) }
}

fun <A> PFAPermission.declareUsage(activity: A, initializer: PFAPermissionDSL)
    where
        A: AppCompatActivity,
        A: PFAPermissionOwner =
    this.declareUsage(activity, org.secuso.pfacore.ui.PFAPermissionAcquirer.Companion.build(activity, initializer))
fun PFAPermission.declareUsage(owner: PFAPermissionOwner, requester: org.secuso.pfacore.ui.PFAPermissionAcquirer): PFAPermissionLauncher {
    owner.registerPFAPermissionInitialization {
        this.initiatePermissionRequestLauncher(requester.activity, requester.handler)
    }
    return requester.request(this)
}

fun <A> List<PFAPermission>.declareUsage(activity: A, initializer: PFAPermissionDSL)
    where
        A: AppCompatActivity,
        A: PFAPermissionOwner =
    this.declareUsage(activity, PFAPermissionAcquirer.build(activity, initializer))
fun List<PFAPermission>.declareUsage(owner: PFAPermissionOwner, requester: org.secuso.pfacore.ui.PFAPermissionAcquirer): PFAPermissionLauncher {
    val permissionStatus = mutableListOf<Pair<PFAPermission, Boolean>>()
    val permissions = this
    val rationaleShown = false

    val permissionAcquirers = this.map {
        val acquirer = org.secuso.pfacore.ui.PFAPermissionAcquirer.Companion.build(requester.activity) {
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

        return@map it.declareUsage(owner, acquirer)
    }
    return {
        permissionAcquirers.forEach { it() }
    }.asLauncher()
}
