package org.secuso.pfacore.ui.stage.builder

import androidx.compose.runtime.Composable
import org.secuso.pfacore.ui.stage.MultiStage
import org.secuso.pfacore.ui.stage.SimpleMultiStageItem
import org.secuso.pfacore.ui.stage.Stage

@Suppress("Unused")
class SimpleMultiStage<S : Stage, M : MultiStage<S>, B : Stage.Builder<S>> : MultiStage.Builder<S, M, B>() {

    fun stage(name: String, initializer: B.() -> Unit) {

        super.add(object : MultiStage.SingleStage<S> {
            override val name: String = name
            override val stage: S = builder.apply(initializer).build()
            override val display: @Composable (onClick: () -> Unit) -> Unit = {
                SimpleMultiStageItem(name = name, onClick = it)
            }

        })
    }
}