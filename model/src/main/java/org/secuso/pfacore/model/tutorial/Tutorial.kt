package org.secuso.pfacore.model.tutorial

import android.app.Activity
import android.content.Intent

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
    val launchActivity: Class<out Activity>? = null,
    val extras: (Intent) -> Intent = { it }
) {
    var onFinish: () -> Unit = { }
    class Builder<TS: TutorialStage, TB: TutorialStage.Builder<TS>>(
        private val builder: () -> TB,
    ) {
        private val stages = mutableListOf<TS>()
        var launchActivity: Class<out Activity>? = null
        var extras: (Intent) -> Intent = { it }
        internal fun build() = Tutorial(stages, launchActivity, extras)
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