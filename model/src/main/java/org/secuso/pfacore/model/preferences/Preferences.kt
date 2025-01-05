package org.secuso.pfacore.model.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.secuso.pfacore.model.preferences.settings.ISettings
import org.secuso.pfacore.model.preferences.Preference as MPreference

/**
 * This class provides declaring both preferences and settings.
 * The class is meant to be build in a declarative way using [Preferences.build].
 *
 * Intended Usage:
 *
 *          Preferences(context, /* supply the concrete Settings-factory */ factory) {
 *              preferences {
 *                  ...
 *              }
 *              settings {
 *                  ...
 *              }
 *          }
 *
 * @see org.secuso.pfacore.model.preferences.settings.Settings
 *
 * @author Patrick Schneider
 */
class Preferences<B, S: ISettings<*>>(private val context: Context, private val factory: (Context, B.() -> Unit) -> S) {
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private var preferences = listOf<Preferable<*>>()
    private var _settings: S? = null
    val settings
        get() = _settings ?: throw java.lang.IllegalStateException("There are no settings defined. Please specify some settings in you preferences.")

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

    fun settings(initializer: B.() -> Unit) {
        if (_settings != null) {
            throw IllegalStateException("You may only specify one set of settings!")
        }
        _settings = factory(context, initializer)
    }


    val all: List<Preferable<*>>
        get() = mutableListOf<Preferable<*>>().apply {
                this.addAll(preferences)
                if (_settings != null) {
                    this.addAll(_settings!!.all.map { it.setting.data }.filterIsInstance<Preferable<*>>())
                }
            }

    companion object {
        fun <B, S: ISettings<*>> build(context: Context, factory: (Context, B.() -> Unit) -> S, initializer: Preferences<B,S>.() -> Unit): Preferences<B,S> {
            return Preferences<B,S>(context, factory).apply(initializer)
        }
    }
}