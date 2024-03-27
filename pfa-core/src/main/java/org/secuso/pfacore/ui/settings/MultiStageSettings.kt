package org.secuso.pfacore.ui.settings

import android.content.Context
import androidx.compose.runtime.Composable
import org.secuso.pfacore.ui.stage.MultiStage
import org.secuso.pfacore.ui.stage.MultiStageMenu

@Suppress("Unused")
class MultiStageSettings(
    override val stages: List<SingleStage<Settings>>
) : MultiStage<Settings>(), ISettings {
    override val composable: @Composable () -> Unit = { MultiStageMenu(stages = stages) }

    override val all: List<Setting<*>>
        get() = stages.flatMap { it.stage.all }

    companion object {

        fun <B : Builder<Settings, MultiStageSettings, Settings.Category>> build(builder: B, context: Context, initializer: B.() -> Unit): MultiStageSettings {
            return build(builder, { Settings.Category(context) }, initializer) {
                MultiStageSettings(it)
            }
        }
    }
}