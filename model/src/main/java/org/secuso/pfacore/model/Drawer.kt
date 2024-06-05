package org.secuso.pfacore.model

import android.app.Activity
import android.content.Intent
import androidx.annotation.DrawableRes

interface DrawerElement {
    val name: String
    val icon: Int?
    fun onClick(activity: Activity)
}
open class ActivityDrawerElement(override val name: String, @DrawableRes override val icon: Int?, private val clazz: Class<out Activity>, private val extras: (Intent) -> Intent = { it }): DrawerElement {
    override fun onClick(activity: Activity) {
        activity.startActivity(extras(Intent(activity, clazz)))
    }
}
open class ActionDrawerElement(override val name: String, @DrawableRes override val icon: Int?, val action: (Activity) -> Unit): DrawerElement {
    override fun onClick(activity: Activity) = action(activity)
}
open class DrawerSection(
    val items: List<DrawerElement>
)
open class DrawerMenu(
    val sections: List<DrawerSection>
)