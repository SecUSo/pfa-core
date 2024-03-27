package org.secuso.pfacore.ui.help

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.secuso.pfacore.ui.BasicInfo
import org.secuso.pfacore.ui.stage.Stage
import kotlin.IllegalStateException

typealias HelpItemComposable = @Composable (@Composable (Modifier) -> Unit, @Composable (Modifier) -> Unit, Modifier) -> Unit

class Help(
    private val items: List<Item>
): Stage {

    override val composable = @Composable { HelpMenu(items) }
    class HelpItem(private val context: Context): Stage.Builder<Help> {
        private val items: MutableList<Item> = mutableListOf()
        internal var composable: HelpItemComposable = @Composable { title, description, modifier ->
            HelpMenuItem(
                title = title,
                description = description,
                modifier
            )
        }
        override fun build() = Help(items)

        fun item(composable: HelpItemComposable = this.composable, initializer: Item.Builder.() -> Unit) {
            this.items.add(Item.Builder(context, composable).apply(initializer).build())
        }
    }

    data class Item(
        internal val title: @Composable (Modifier) -> Unit,
        internal val description: @Composable (Modifier) -> Unit,
        internal val composable: HelpItemComposable
    ) {
        class Builder(
            private val context: Context,
            private val composable: HelpItemComposable
        ) {
            private var title: (@Composable (Modifier) -> Unit)? = null
            private var description: (@Composable (Modifier) -> Unit)? = null

            fun build() = Item(
                title ?: throw IllegalStateException("An item in the help section needs to have a title"),
                description ?: throw IllegalStateException("An item in the help section needs to have description"),
                composable
            )

            fun title(initializer: BasicInfo.() -> Unit) {
                this.title = BasicInfo(context.resources) {
                    { modifier -> HelpTitle(text = it, modifier = modifier) }
                }.apply(initializer).build()
            }

            fun description(initializer: BasicInfo.() -> Unit) {
                this.description = BasicInfo(context.resources) {
                    { modifier -> HelpDescription(text = it, modifier = modifier) }
                }.apply(initializer).build()
            }
        }
    }

    companion object {
        fun build(context: Context, initializer: HelpItem.() -> Unit): Help {
            return HelpItem(context).apply(initializer).build()
        }
    }
}