package org.secuso.pfacore.model.preferences

import android.util.JsonReader
import androidx.lifecycle.MutableLiveData
import org.secuso.pfacore.backup.Restorer

interface PreferableBuildInfo<T> {
    var default: T?
    var key: String?
    var backup: Boolean
}
internal fun <T> PreferableBuildInfo<T>.validate() = when {
    default == null -> throw IllegalStateException("A preference needs a defaultValue")
    key == null -> throw IllegalStateException("A preference needs a key")
    else -> Unit
}

interface Preferable<T> {
    val default: T
    val key: String
    val backup: Boolean
    var value: T?
    fun restore(reader: JsonReader)
}

open class PreferenceBuildInfo<T>: PreferableBuildInfo<T> {
    override var default: T? = null
    override var key: String? = null
    override var backup: Boolean = false
}

open class Preference<T>(
    val state: MutableLiveData<T>,
    override val default: T,
    override val key: String,
    override val backup: Boolean,
    private val restorer: Restorer<T>,
    private val onUpdate: (T) -> Unit
): Preferable<T> {
    override var value
        get() = state.value
        set(value) {
            state.value = value!!
            onUpdate(value)
        }

    override fun restore(reader: JsonReader) {
        value = restorer(reader)
    }
}