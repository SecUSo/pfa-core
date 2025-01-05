package org.secuso.pfacore.model

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import androidx.annotation.DrawableRes
import kotlin.properties.Delegates

/**
 * Define something that behaves like a Drawer.
 * This is intended to be used as an extension for an activity.
 *
 * @see DrawerMenu
 * @see DrawerElement
 *
 * @author Patrick Schneider
 */
interface Drawer {
    fun drawer(): DrawerMenu
    fun defaultDrawerSection(builder: DrawerMenu.Builder) {}
    fun isActiveDrawerElement(element: DrawerElement): Boolean
}

/**
 * Define how a drawer element should behave and look like.
 * It either launches an other activity or performs an action.
 *
 * @see ActivityDrawerElement
 * @see ActionDrawerElement
 *
 *  @see name The name to be shown on the drawer element
 *  @see icon An optional icon to be shown at the start of the drawer element
 *
 * @author Patrick Schneider
 */
sealed interface DrawerElement {
    val name: String
    val icon: Int?
    fun onClick(activity: Activity)
}

/**
 * A drawer element which will launch the given activity.
 *
 *  @see name The name to be shown on the drawer element
 *  @see icon An optional icon to be shown at the start of the drawer element
 *  @see clazz The activity to be launched
 *  @see extras Optionally customise the intent which will be used to launch the activity
 */
open class ActivityDrawerElement(override val name: String, @DrawableRes override val icon: Int?, private val clazz: Class<out Activity>, private val extras: (Intent) -> Intent = { it }): DrawerElement {
    override fun onClick(activity: Activity) {
        if (activity.javaClass == clazz) {
            return
        }
        activity.startActivity(extras(Intent(activity, clazz)))
    }

    class Builder {
        lateinit var name: String
        @DrawableRes var icon: Int? = null
        lateinit var clazz: Class<out Activity>
        var extras: (Intent) -> Intent = { it }

        internal fun build() = ActivityDrawerElement(name, icon, clazz, extras)
    }
}
/**
 * A drawer element which will perform the given action.
 *
 * @see name The name to be shown on the drawer element
 * @see icon An optional icon to be shown at the start of the drawer element
 * @see action The action to be executed with the context of the current Activity.
 *
 * @author Patrick Schneider
 */
open class ActionDrawerElement(override val name: String, @DrawableRes override val icon: Int?, val action: (Activity) -> Unit): DrawerElement {
    override fun onClick(activity: Activity) = action(activity)

    class Builder {
        lateinit var name: String
        @DrawableRes var icon: Int? = null
        lateinit var onClick: (Activity) -> Unit

        internal fun build() = ActionDrawerElement(name, icon, onClick)
    }
}

/**
 * A drawer section containing the corresponding drawer elements.
 *
 * @author Patrick Schneider
 */
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

/**
 * The contents describing what the drawer menu should look like.
 *
 * This class is intended to be created in a declarative way.
 * Intended Usage:
 *
 *       val drawer = DrawerMenu.build {
 *              name = getString(R.string.app_name)
 *              icon = R.mipmap.ic_launcher
 *              section {
 *                  activity {
 *                      name = getString(R.string.action_main)
 *                      icon = R.drawable.ic_menu_home
 *                      clazz = MainActivity::class.java
 *                      extras = { it.apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TOP } }
 *                  }
 *                  activity {
 *                      name = getString(R.string.action_game)
 *                      icon = R.drawable.ic_menu_game
 *                      clazz = GameActivity::class.java
 *                  }
 *              }
 *              defaultDrawerSection(this)
 *          }
 *
 * @author Patrick Schneider
 * @see DrawerMenu.Builder
 * @see DrawerSection
 * @see DrawerElement
 */
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