package org.secuso.pfacore.model.preferences

import android.content.SharedPreferences
import org.secuso.pfacore.model.preferences.settings.ISettings
import org.secuso.pfacore.model.preferences.Preference as MPreference

class Preferences<S: ISettings<*>>(private val sharedPreferences: SharedPreferences) {
    private var preferences = listOf<Preferable<*>>()

    class Preference(private val sharedPreferences: SharedPreferences) {
        val preferences = mutableListOf<Preferable<*>>()
        fun <T> preference(info: PreferenceBuildInfo<T>.() -> Unit): Preferable<T> {
            return PreferenceBuildInfo<T>().apply(info).build(sharedPreferences) { state, restorer, onUpdate ->
                InfoFactory<PreferenceBuildInfo<T>, Preferable<T>> { info ->
                    {
                        MPreference<T>(
                            state(info.key!!, info.default!!),
                            info.default!!,
                            info.key!!,
                            info.backup,
                            restorer(info.default!!),
                            onUpdate(info.key!!, info.default!!, info.onUpdate),
                        )
                    }
                }
            }.apply { preferences.add(this) }
        }
    }

    fun preferences(initializer: Preference.() -> Unit) {
        if (preferences.isNotEmpty()) {
            throw IllegalStateException("You may only specify one set of preferences!")
        }
        preferences = Preference(sharedPreferences).apply(initializer).preferences
    }

    var settings: S? = null

    val all: List<Preferable<*>>
        get() = mutableListOf(preferences).apply {
                if (settings != null) {
                    this.add(settings!!.all.map { it.setting.data }.filterIsInstance<Preferable<*>>())
                }
            }.flatten()

    companion object {
        fun <S: ISettings<*>> build(sharedPreferences: SharedPreferences, initializer: Preferences<S>.() -> Unit): Preferences<S> {
            return Preferences<S>(sharedPreferences).apply(initializer)
        }
    }
}