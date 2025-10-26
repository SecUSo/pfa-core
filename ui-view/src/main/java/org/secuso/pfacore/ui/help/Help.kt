package org.secuso.pfacore.ui.help

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import org.secuso.pfacore.ui.BasicInfo
import org.secuso.pfacore.ui.Inflatable
import org.secuso.ui.view.databinding.HelpDescriptionBinding
import org.secuso.ui.view.databinding.HelpTitleBinding
import org.secuso.pfacore.model.help.Help as MHelp

class Help(override val items: List<HelpData>) : MHelp<HelpData>(items) {

    fun build(layoutInflater: LayoutInflater, owner: LifecycleOwner) = HelpAdapter(items, layoutInflater, owner)

    class Item(val resources: Resources): MHelp.Item<HelpData>() {
        internal var title: Inflatable? = null
        internal var description: Inflatable? = null

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
            title = BasicInfo(resources) { text -> Inflatable { inflater: LayoutInflater, root: ViewGroup?, _ ->
                HelpTitleBinding.inflate(inflater, root, false).apply { this.text = text }.root
            } }.apply(initializer).build()
        }

        fun description(initializer: BasicInfo.() -> Unit) {
            description = BasicInfo(resources) { text -> Inflatable { inflater: LayoutInflater, root: ViewGroup?, _ ->
                HelpDescriptionBinding.inflate(inflater, root, false).apply { this.text = text }.root
            } }.apply(initializer).build()
        }

        fun descriptions(context: Context, descs: List<Int>) {
            description = BasicInfo(resources, { Inflatable { _, _, _ -> View(context) } }).apply {
                custom { inflater, root, owner ->
                    val layout = LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL
                        showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
                    }
                    for (desc in descs) {
                        layout.addView(HelpDescriptionBinding.inflate(inflater, layout, false).apply { this.text = context.getString(desc) }.root)
                    }

                    layout.rootView
                }
            }.build()
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