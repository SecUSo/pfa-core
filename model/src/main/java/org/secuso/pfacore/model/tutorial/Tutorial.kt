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

/**
 * This class defines all necessary information needed to represent the Tutorial-Activity of a PFA.
 * It has multiple [Stages][TutorialStage] and launches a final activity at the end.
 *
 * The `ui-*` libraries provides methods to instantiate and show the tutorial.
 *
 * The class is meant to be build by using it's [build][Tutorial.build] method.
 *
 * Intended Usage:
 *
 *          val tutorial = Tutorial.build(/* provide your stage builder */ builder) {
 *               stage {
 *                   title = context.getString(R.string.slide1_heading)
 *                   images = listOf(R.mipmap.ic_splash)
 *                   description = context.getString(R.string.slide1_text)
 *               }
 *               stage {
 *                   title = context.getString(R.string.slide2_heading)
 *                   images = listOf(R.mipmap.ic_splash)
 *                   description = context.getString(R.string.slide2_text)
 *               }
 *           }
 *
 * @param stages The [Stages][TutorialStage] of the tutorial.
 * @param launchActivity The activity to be launched after the tutorial.
 * @param extras Any customization to the [Tutorial.launchActivity].
 * @property onFinish A listener to be invoked if the tutorial is finished.
 *
 * @author Patrick Schneider.
 */
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