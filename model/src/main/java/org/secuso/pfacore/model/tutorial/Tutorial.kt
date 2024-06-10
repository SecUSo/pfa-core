package org.secuso.pfacore.model.tutorial

open class TutorialStage(
    val title: String,
    val images: List<Int>,
    val description: String?,
    open val requirements: () -> Boolean
) {
    abstract class Builder<TS: TutorialStage> {
        lateinit var title: String
        var images: List<Int> = listOf()
        var description: String? = null
        var requirements: () -> Boolean = { true }

        abstract fun build(): TS
    }
}

open class Tutorial<TS: TutorialStage>(
    val stages: List<TS>,
) {
    class Builder<TS: TutorialStage, TB: TutorialStage.Builder<TS>>(
        private val builder: () -> TB,
    ) {
        private val stages = mutableListOf<TS>()
        internal fun build() = Tutorial(stages)
        fun stage(initializer: TB.() -> Unit) {
            stages.add(builder().apply(initializer).build())
        }
    }

    companion object {
        fun <TS: TutorialStage, TB: TutorialStage.Builder<TS>> build(
            stageBuilder: () -> TB,
            initializer: Builder<TS, TB>.() -> Unit
        ): Tutorial<TS> = Builder<TS,TB>(stageBuilder).apply(initializer).build()
    }
}