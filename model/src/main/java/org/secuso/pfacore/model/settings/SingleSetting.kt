package org.secuso.pfacore.model.settings

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import org.secuso.pfacore.backup.Restorer

abstract class SingleSetting<T, S : ISetting<T, S>>(
    private val resources: Resources,
    private val type: (data: SettingData<T>, backup: Boolean, restorer: Restorer<T>) -> Setting<T, S>,
    var key: String? = null,
    var default: T? = null,
    var depends: String? = null,
    var onUpdate: ((T) -> Unit)? = null,
    var backup: Boolean = true,
) {
    private var entries: List<SettingEntry<T>>? = null

    @Suppress("Unused")
    fun entries(initializer: Entries<T>.() -> Unit) {
        this.entries = Entries<T>(resources).apply(initializer).collect()
    }

    abstract fun adapt(setting: Setting<T, S>): S

    fun compose(
        state: (String, T) -> MutableLiveData<T>,
        update: (String, T, T) -> Unit,
        enabled: (String?) -> MutableLiveData<Boolean>,
        restorer: Restorer<T>,
    ): S {
        val data = when {
            key === null -> throw IllegalStateException("A setting needs to have a key")
            default === null -> throw IllegalStateException("A setting needs to have a default value")
            else -> SettingData(
                key = key!!,
                state = state(key!!, default!!),
                defaultValue = default!!,
                entries = entries,
                onUpdate = { value -> update(key!!, default!!, value); onUpdate?.let { it(value) } },
                enable = enabled(depends)
            )
        }
        return adapt(type(data, backup, restorer))
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