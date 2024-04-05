package org.secuso.pfacore.model.help

open class Help<H: IHelpData<H>>(protected val items: List<H>) {

    class HelpItem<H: IHelpData<H>, I: Item<H>>(
        private val builder: () -> I
    ) {
        internal val items: MutableList<H> = mutableListOf()
        fun item(initializer: I.() -> Unit) {
            this.items.add(builder().apply(initializer).adapt())
        }
    }

    abstract class Item<H: IHelpData<H>> {
        abstract fun adapt(): H
    }

    companion object {
        fun <H: IHelpData<H>, I: Item<H>, Hel: Help<H>> build(
            builder: () -> I,
            helpBuilder: (List<H>) -> Hel,
            initializer: HelpItem<H, I>.() -> Unit): Hel {
            return helpBuilder(HelpItem(builder).apply(initializer).items)
        }
    }
}