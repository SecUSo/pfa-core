package org.secuso.pfacore.ui.compose.settings

import android.content.res.Resources
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.secuso.pfacore.backup.Restorer
import org.secuso.pfacore.model.settings.Setting
import org.secuso.pfacore.model.settings.SettingData
import org.secuso.pfacore.ui.compose.TransformableInfo
import org.secuso.pfacore.model.settings.SingleSetting as MSingleSetting

class SingleSetting<T>(private val resources: Resources, type: (SettingData<T>, Boolean, Restorer<T>) -> Setting<T, SettingDecorator<T>>) :
    MSingleSetting<T, SettingDecorator<T>>(resources, type) {
    private var title: (@Composable (SettingData<T>, T, Modifier) -> Unit)? = null
    private var summary: (@Composable (SettingData<T>, T, Modifier) -> Unit)? = null
    override fun adapt(setting: Setting<T, SettingDecorator<T>>): SettingDecorator<T> {
        return when {
            title === null -> throw IllegalStateException("A setting needs a title")
            summary === null -> throw IllegalStateException("A setting needs a summary")
            else -> SettingDecorator(setting as DisplayableInnerSetting<T, SettingDecorator<T>>, title!!, summary!!)
        }
    }

    @Suppress("Unused")
    fun title(initializer: TransformableInfo<SettingData<T>, T>.() -> Unit) {
        this.title = TransformableInfo<SettingData<T>, T>(resources) { transformer ->
            { data, value, modifier -> Text(text = transformer(data, value), modifier = modifier) }
        }.apply(initializer).build()
    }

    @Suppress("Unused")
    fun summary(initializer: TransformableInfo<SettingData<T>, T>.() -> Unit) {
        this.summary = TransformableInfo<SettingData<T>, T>(resources) { transformer ->
            { data, state, modifier -> Text(text = transformer(data, state), modifier = modifier) }
        }.apply(initializer).build()
    }
}