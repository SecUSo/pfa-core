package org.secuso.privacyfriendlycore.ui.settings.builder

import android.content.res.Resources
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import org.secuso.privacyfriendlycore.ui.composables.SummaryText
import org.secuso.privacyfriendlycore.ui.settings.SettingData
import org.secuso.privacyfriendlycore.ui.settings.SettingEntry

class SingleSetting<T>(
    private val resources: Resources,
    var key: String? = null,
    var default: T? = null,
    var depends: String? = null,
    var onUpdate: ((T) -> Unit)? = null
) {
    private var entries: List<SettingEntry<T>>? = null
    private var title: (@Composable (SettingData<T>, T, Modifier) -> Unit)? = null
    private var summary: (@Composable (SettingData<T>, T, Modifier) -> Unit)? = null

    @Suppress("Unused")
    fun entries(initializer: Entries<T>.() -> Unit) {
        this.entries = Entries<T>(resources).apply(initializer).collect()
    }

    @Suppress("Unused")
    fun title(initializer: Info<T>.() -> Unit) {
        this.title = Info<T>(resources) { transformer ->
            { data, value, modifier -> Text(text = transformer(data, value), modifier = modifier) }
        }.apply(initializer).build()
    }

    @Suppress("Unused")
    fun summary(initializer: Info<T>.() -> Unit) {
        this.summary = Info<T>(resources) { transformer ->
            { data, value, modifier -> SummaryText(text = transformer(data, value), modifier = modifier) }
        }.apply(initializer).build()
    }

    fun compose(
        state: (SingleSetting<T>) -> MutableState<T>,
        enabled: (String?) -> MutableState<Boolean>,
        composable: (SingleSetting<T>) -> @Composable (data: SettingData<T>) -> Unit
    ): SettingData<T> {
        return when {
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

    class Info<T>(
        private val resources: Resources,
        private val default: ((SettingData<T>, T) -> String) -> (@Composable (SettingData<T>, T, Modifier) -> Unit)
    ) {
        private var composable: (@Composable (SettingData<T>, T, Modifier) -> Unit)? = null

        fun build() = this.composable!!

        @Suppress("Unused")
        fun resource(id: Int) {
            this.composable = default { _, _ -> resources.getString(id) }
        }

        @Suppress("Unused")
        fun literal(text: String) {
            this.composable = default { _, _ -> text }
        }

        @Suppress("Unused")
        fun transform(transformer: (SettingData<T>, T) -> String) {
            this.composable = default(transformer)
        }

        @Suppress("Unused")
        fun custom(composable: (@Composable (SettingData<T>, T, Modifier) -> Unit)) {
            this.composable = composable
        }
    }
}