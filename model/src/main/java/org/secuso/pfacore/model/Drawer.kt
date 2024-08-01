package org.secuso.pfacore.model

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import androidx.annotation.DrawableRes
import kotlin.properties.Delegates

interface Drawer {
    fun drawer(): DrawerMenu
    fun defaultDrawerSection(builder: DrawerMenu.Builder) {}
    fun isActiveDrawerElement(element: DrawerElement): Boolean
}

sealed interface DrawerElement {
    val name: String
    val icon: Int?
    fun onClick(activity: Activity)
}
open class ActivityDrawerElement(override val name: String, @DrawableRes override val icon: Int?, private val clazz: Class<out Activity>, private val extras: (Intent) -> Intent = { it }): DrawerElement {
    override fun onClick(activity: Activity) {
        if (activity.javaClass == clazz) {
            return
        }
        activity.startActivity(extras(Intent(activity, clazz)))
        activity.finish()
    }

    class Builder {
        lateinit var name: String
        @DrawableRes var icon: Int? = null
        lateinit var clazz: Class<out Activity>
        var extras: (Intent) -> Intent = { it }

        internal fun build() = ActivityDrawerElement(name, icon, clazz, extras)
    }
}
open class ActionDrawerElement(override val name: String, @DrawableRes override val icon: Int?, val action: (Activity) -> Unit): DrawerElement {
    override fun onClick(activity: Activity) = action(activity)

    class Builder {
        lateinit var name: String
        @DrawableRes var icon: Int? = null
        lateinit var onClick: (Activity) -> Unit

        internal fun build() = ActionDrawerElement(name, icon, onClick)
    }
}
open class DrawerSection(
    val items: List<DrawerElement>
) {
    class Builder {
        private val items = mutableListOf<DrawerElement>()

        internal fun build() = DrawerSection(items)

        fun activity(initializer: ActivityDrawerElement.Builder.() -> Unit) {
            items.add(ActivityDrawerElement.Builder().apply(initializer).build())
        }

        fun action(initializer: ActionDrawerElement.Builder.() -> Unit) {
            items.add(ActionDrawerElement.Builder().apply(initializer).build())
        }
    }
}
open class DrawerMenu(
    val sections: List<DrawerSection>,
    val name: String,
    @DrawableRes val icon: Int
) {
    class Builder() {
        private val sections = mutableListOf<DrawerSection>()
        lateinit var name: String

        var icon by Delegates.notNull<Int>()
        internal fun build() = DrawerMenu(sections, name, icon)

        fun section(initializer: DrawerSection.Builder.() -> Unit) {
            sections.add(DrawerSection.Builder().apply(initializer).build())
        }
    }

    companion object {
        fun build(initializer: Builder.() -> Unit) = Builder().apply(initializer).build()
    }
}