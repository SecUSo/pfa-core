package org.secuso.privacyfriendlycore.ui.stage

import androidx.compose.runtime.Composable

abstract class MultiStage<S : Stage> : Stage {
    abstract val stages: List<SingleStage<S>>
    override val composable: @Composable () -> Unit = { MultiStageMenu(stages = stages) }

    abstract class Builder<S : Stage, M : MultiStage<S>, B : Stage.Builder<S>> {
        internal var _builder: (() -> B)? = null
        val builder: B
            get() = this._builder?.let { it() } ?: throw IllegalStateException("The Stage.Builder has to be provided for the MultiStage.Builder")

        private val stages: MutableList<SingleStage<S>> = mutableListOf()
        fun build(factory: (stages: List<SingleStage<S>>) -> M): M = factory(this.stages)
        fun add(stage: SingleStage<S>) {
            this.stages.add(stage)
        }
    }

    interface SingleStage<S : Stage> {
        val name: String
        val stage: S
        val display: @Composable (onClick: () -> Unit) -> Unit
    }

    companion object {
        @Suppress("Unused")
        fun <S : Stage, M : MultiStage<S>, SB : Stage.Builder<S>, B : Builder<S, M, SB>> build(
            builder: B,
            stageBuilder: () -> SB,
            initializer: B.() -> Unit,
            factory: (stages: List<SingleStage<S>>) -> M,
        ): M {
            return builder.apply { this._builder = stageBuilder }.apply(initializer).build(factory)
        }
    }
}