package org.secuso.pfacore.ui.compose.help

import android.content.Context
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.secuso.pfacore.ui.compose.BasicInfo
import org.secuso.pfacore.ui.compose.Displayable
import org.secuso.pfacore.model.help.Help as MHelp

class Help(items: List<HelpData>) : MHelp<HelpData>(items), Displayable {

    @Composable
    override fun Display(onClick: (() -> Unit)?) {
        HelpMenu(items = items)
    }

    class Item(val resources: Resources) : MHelp.Item<HelpData>() {
        internal var title: (@Composable (Modifier) -> Unit)? = null
        internal var description: (@Composable (Modifier) -> Unit)? = null

        override fun adapt(): HelpData {
            return when {
                title === null -> throw IllegalStateException("A HelpItem needs a title.")
                description === null -> throw IllegalStateException("A HelpItem needs a summary.")
                else -> {
                    HelpData(title!!, description!!)
                }
            }
        }

        fun title(initializer: BasicInfo.() -> Unit) {
            title = BasicInfo(resources) { text -> { modifier -> HelpTitle(text = text, modifier = modifier) } }.apply(initializer).build()
        }

        fun description(initializer: BasicInfo.() -> Unit) {
            description = BasicInfo(resources) { text -> { modifier -> HelpDescription(text = text, modifier = modifier) } }.apply(initializer).build()
        }
    }

    companion object {
        fun build(context: Context, initializer: HelpItem<HelpData, Item>.() -> Unit): Help {
            return MHelp.build<HelpData, Item, Help>(
                builder = { Item(context.resources) },
                helpBuilder = { Help(it) },
                initializer = initializer
            )
        }
    }
}