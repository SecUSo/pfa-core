package org.secuso.pfacore.ui.stage.builder

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import org.secuso.pfacore.ui.stage.IconMultiStageItem
import org.secuso.pfacore.ui.stage.MultiStage
import org.secuso.pfacore.ui.stage.Stage

@Suppress("unused")
class IconMultiStage<S : Stage, M : MultiStage<S>, B : Stage.Builder<S>> : MultiStage.Builder<S, M, B>() {
    fun stage(icon: ImageVector, name: String, initializer: B.() -> Unit) {
        super.add(object : MultiStage.SingleStage<S> {
            override val name: String = name
            override val stage: S = builder.apply(initializer).build()
            override val display: @Composable (onClick: () -> Unit) -> Unit = {
                IconMultiStageItem(icon = icon, name = name, onClick = it)
            }
        })
    }

}