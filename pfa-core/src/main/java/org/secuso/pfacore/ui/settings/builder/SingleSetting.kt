package org.secuso.pfacore.ui.settings.builder

import android.content.res.Resources
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import org.secuso.pfacore.backup.Restorer
import org.secuso.pfacore.ui.TransformableInfo
import org.secuso.pfacore.ui.composables.SummaryText
import org.secuso.pfacore.ui.settings.Setting
import org.secuso.pfacore.ui.settings.SettingData
import org.secuso.pfacore.ui.settings.SettingEntry

class SingleSetting<T>(
    private val resources: Resources,
    var key: String? = null,
    var default: T? = null,
    var depends: String? = null,
    var onUpdate: ((T) -> Unit)? = null,
    var backup: Boolean = true,
) {
    private var entries: List<SettingEntry<T>>? = null
    private var title: (@Composable (SettingData<T>, T, Modifier) -> Unit)? = null
    private var summary: (@Composable (SettingData<T>, T, Modifier) -> Unit)? = null

    @Suppress("Unused")
    fun entries(initializer: Entries<T>.() -> Unit) {
        this.entries = Entries<T>(resources).apply(initializer).collect()
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
            { data, state, modifier -> SummaryText(text = transformer(data, state), modifier = modifier) }
        }.apply(initializer).build()
    }

    fun compose(
        state: (SingleSetting<T>) -> MutableState<T>,
        enabled: (String?) -> MutableState<Boolean>,
        restorer: Restorer<T>,
        composable: (SingleSetting<T>) -> @Composable (data: SettingData<T>) -> Unit
    ): Setting<T> {
        val data = when {
            key === null -> throw IllegalStateException("A setting needs to have a key")
            default === null -> throw IllegalStateException("A setting needs to have a default value")
            title === null -> throw IllegalStateException("A setting needs a title")
            else -> SettingData(
                key = key!!,
                state = state(this),
                defaultValue = default!!,
                title = this.title!!,
                summary = this.summary ?: { _, _, _ -> },
                _composable = composable(this),
                entries = entries,
                enable = enabled(depends)
            )
        }
        return Setting(data, backup, restorer)
    }

    class Entries<T>(
        private val resources: Resources,
        private var entries: List<String>? = null,
        private var values: List<T>? = null
    ) {
        fun collect() = entries!!.zip(values!!).map { (entry, value) -> SettingEntry(entry, value) }.toList()

        @Suppress("Unused")
        fun entries(entries: List<String>) {
            this.entries = entries
        }

        @Suppress("Unused")
        fun entries(id: Int) {
            this.entries = resources.getStringArray(id).toList()
        }

        @Suppress("Unused")
        fun values(values: List<T>) {
            this.values = values
        }
    }
}