package org.secuso.pfacore.model.preferences

import android.content.SharedPreferences
import android.util.JsonReader
import android.util.Log
import androidx.lifecycle.MutableLiveData
import org.secuso.pfacore.backup.Restorer
import org.secuso.pfacore.backup.booleanRestorer
import org.secuso.pfacore.backup.doubleRestorer
import org.secuso.pfacore.backup.floatRestorer
import org.secuso.pfacore.backup.intRestorer
import org.secuso.pfacore.backup.noRestorer
import org.secuso.pfacore.backup.stringRestorer

interface Info
interface BuildInfo

interface PreferableBuildInfo<T> : BuildInfo {
    var default: T?
    var key: String?
    var backup: Boolean
    var onUpdate: (T) -> Unit
}
internal fun <T> PreferableBuildInfo<T>.validate() = when {
    default == null -> throw IllegalStateException("A preference needs a defaultValue")
    key == null -> throw IllegalStateException("A preference needs a key")
    else -> Unit
}

interface Preferable<T> : Info {
    val state: MutableLiveData<T>
    val default: T
    val key: String
    val backup: Boolean
    var value: T
    fun restore(reader: JsonReader)
    val onUpdate: (T) -> Unit
}

open class PreferenceBuildInfo<T>: PreferableBuildInfo<T> {
    override var default: T? = null
    override var key: String? = null
    override var backup: Boolean = false
    override var onUpdate: (T) -> Unit = {}
}

open class Preference<T>(
    override val state: MutableLiveData<T>,
    override val default: T,
    override val key: String,
    override val backup: Boolean,
    private val restorer: Restorer<T>,
    override val onUpdate: (T) -> Unit
): Preferable<T> {
    override var value
        get() = state.value ?: default
        set(value) {
            state.value = value
            onUpdate(value)
        }

    override fun restore(reader: JsonReader) {
        value = restorer(reader)
    }
}

fun interface InfoFactory<BI: BuildInfo, SI: Info> {
    fun build(info: BI): () -> SI
}
typealias DeriveState<T> = (String, T) -> MutableLiveData<T>
typealias DataSaverUpdater<T> = (String, T, (T) -> Unit) -> (T) -> Unit
typealias PreferenceFactory<T, BI, SI> = (DeriveState<T>, (T) -> Restorer<T>, DataSaverUpdater<T>) -> InfoFactory<BI, SI>
fun <T, BI: BuildInfo, SI: Info> BI.build(preferences: SharedPreferences, factory: PreferenceFactory<T, BI, SI>): SI {
    val state = { key: String, value: T ->
        @Suppress("IMPLICIT_CAST_TO_ANY")
        MutableLiveData(
            when (value) {
                is Unit -> Unit
                is Boolean -> preferences.getBoolean(key, value as Boolean)
                is String -> preferences.getString(key, value as String)
                is Int -> preferences.getInt(key, value as Int)
                is Float -> preferences.getFloat(key, value as Float)
                is Double -> Double.fromBits(preferences.getLong(key, (value as Double).toRawBits()))
                else -> throw UnsupportedOperationException("The given type ${value!!::class.java} is no valid setting type")
            } as T
        )
    }
    val update: DataSaverUpdater<T> = { key: String, default: T, onUpdate: (T) -> Unit -> { value: T ->
        preferences.edit().apply {
            Log.d("Saving setting", "key: ${key}, value: $value")
            when (default) {
                is Boolean -> putBoolean(key, value as Boolean)
                is String -> putString(key, value as String)
                is Int -> putInt(key, value as Int)
                is Float -> putFloat(key, value as Float)
                is Double -> putLong(key, (value as Double).toRawBits())
                is Unit -> {}
                else -> throw UnsupportedOperationException("The given type ${default!!::class.java} is no valid setting type")
            }
        }.apply()
        onUpdate(value)
    }
    }
    val restorer = { value: T ->
        when (value) {
            is Boolean -> booleanRestorer
            is String -> stringRestorer
            is Int -> intRestorer
            is Float -> floatRestorer
            is Double -> doubleRestorer
            is Unit -> noRestorer
            else -> throw UnsupportedOperationException("The given type ${value!!::class.java} cannot be restored")
        } as Restorer<T>
    }
    return factory(state, restorer, update).build(this).invoke()
}