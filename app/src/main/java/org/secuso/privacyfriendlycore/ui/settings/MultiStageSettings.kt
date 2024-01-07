package org.secuso.privacyfriendlycore.ui.settings

import android.content.Context
import org.secuso.privacyfriendlycore.ui.stage.MultiStage

@Suppress("Unused")
class MultiStageSettings(
    override val stages: List<SingleStage<Settings>>
) : MultiStage<Settings>(), ISettings {

    override val all: List<SettingData<*>>
        get() = stages.flatMap { it.stage.all }

    companion object {

        fun <B : Builder<Settings, MultiStageSettings, Settings.Category>> build(builder: B, context: Context, initializer: B.() -> Unit): MultiStageSettings {
            return build(builder, { Settings.Category(context) }, initializer) {
                MultiStageSettings(it)
            }
        }
    }
}