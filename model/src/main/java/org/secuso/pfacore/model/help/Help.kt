package org.secuso.pfacore.model.help

/**
 * The data needed to display the help section.
 * A instance of this class is intended to be built using a declarative approach:
 *
 *      val help = Help.build(context) {
 *          item {
 *              title { resource(R.string.help_whatis) }
 *              description { resource(R.string.help_whatis_answer) }
 *          }
 *          item {
 *              title { resource(R.string.help_feature_one) }
 *              description { resource(R.string.help_feature_one_answer) }
 *          }
 *      }
 *
 * @see Help.build
 * @author Patrick Schneider
 */
open class Help<H : IHelpData<H>>(protected open val items: List<H>) {

    class HelpItem<H : IHelpData<H>, I : Item<H>>(
        private val builder: () -> I
    ) {
        internal val items: MutableList<H> = mutableListOf()
        fun item(initializer: I.() -> Unit) {
            this.items.add(builder().apply(initializer).adapt())
        }
    }

    abstract class Item<H : IHelpData<H>> {
        abstract fun adapt(): H
    }

    companion object {
        fun <H : IHelpData<H>, I : Item<H>, Hel : Help<H>> build(
            builder: () -> I,
            helpBuilder: (List<H>) -> Hel,
            initializer: HelpItem<H, I>.() -> Unit
        ): Hel {
            return helpBuilder(HelpItem(builder).apply(initializer).items)
        }
    }
}