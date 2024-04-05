package org.secuso.pfacore.model.settings

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.secuso.pfacore.backup.Restorer
import org.secuso.pfacore.backup.booleanRestorer
import org.secuso.pfacore.backup.doubleRestorer
import org.secuso.pfacore.backup.floatRestorer
import org.secuso.pfacore.backup.intRestorer
import org.secuso.pfacore.backup.serializableRestorer
import org.secuso.pfacore.backup.stringRestorer

interface ISettings<S : ISetting<*, S>> {
    @Suppress("unused")
    val all: List<S>
}

abstract class Settings<I : ISetting<*, I>, SHC : SettingCategory<I>, SHM : SettingMenu<I>>(internal val settings: List<SHC>) : ISettings<I> {
    override val all: List<I>
        get() = settings.map { it.all() }.flatten()

    data class SettingsBuilders<
            I : ISetting<Any, I>,
            SS : SingleSetting<Any, I>,
            SHC : SettingCategory<I>,
            SHM : SettingMenu<I>,
            S : Setting<I, SS, SHC, SHM, S, C, M>,
            C : Category<I, SS, SHC, SHM, S, C, M>,
            M : Menu<I, SS, SHC, SHM, S, C, M>,
            >(
        private val _setting: (SettingsBuilders<I, SS, SHC, SHM, S, C, M>) -> S,
        private val _category: (SettingsBuilders<I, SS, SHC, SHM, S, C, M>) -> C,
        private val _menu: (SettingsBuilders<I, SS, SHC, SHM, S, C, M>) -> M,
        val shc: (String, C) -> SHC,
        val shm: (String, M) -> SHM
    ) {
        val setting
            get() = _setting(this)
        val category
            get() = _category(this)
        val menu
            get() = _menu(this)
    }

    abstract class Setting<
            I : ISetting<Any, I>,
            SS : SingleSetting<Any, I>,
            SHC : SettingCategory<I>,
            SHM : SettingMenu<I>,
            S : Setting<I, SS, SHC, SHM, S, C, M>,
            C : Category<I, SS, SHC, SHM, S, C, M>,
            M : Menu<I, SS, SHC, SHM, S, C, M>
            >(
        val settings: MutableList<SettingHierarchy<I>> = mutableListOf(),
        val resources: Resources,
        val builders: SettingsBuilders<I, SS, SHC, SHM, S, C, M>
    ) {
        inline fun <reified T, IS : ISetting<T, IS>, SS : SingleSetting<T, IS>> SS.create(preferences: SharedPreferences): I {
            if (this.default == null) {
                throw IllegalStateException("A default has to be set for a setting.")
            }
            val restorer: Restorer<T> = when (this.default) {
                is Boolean -> booleanRestorer
                is String -> stringRestorer
                is Int -> intRestorer
                is Float -> floatRestorer
                is Double -> doubleRestorer
                else -> serializableRestorer()
            } as Restorer<T>
            val state: (String, T) -> MutableLiveData<T> = { key, default ->
                MutableLiveData(
                    when (this.default) {
                        is Boolean -> preferences.getBoolean(key, default!! as Boolean)
                        is String -> preferences.getString(key, default!! as String)
                        is Int -> preferences.getInt(key, default!! as Int)
                        is Float -> preferences.getFloat(key, default!! as Float)
                        is Double -> Double.fromBits(preferences.getLong(key, (default!! as Double).toRawBits()))
                        else -> preferences.getString(key, null)?.let { Json.decodeFromString(it) } ?: default!!
                    } as T
                )
            }
            val update: (String, T, T) -> Unit = { key, default, value ->
                preferences.edit().apply {
                    Log.d("Saving setting", "key: ${key}, value: $value")
                    when (default) {
                        is Boolean -> putBoolean(key, value as Boolean)
                        is String -> putString(key, value as String)
                        is Int -> putInt(key, value as Int)
                        is Float -> putFloat(key, value as Float)
                        is Double -> putLong(key, (value as Double).toRawBits())
                        else -> putString(key, Json.encodeToString(value))
                    }
                }.apply()
            }
            val enabled = { dependency: String? ->
                if (dependency == null) {
                    MutableLiveData(true)
                } else {
                    val state = settings.map { it.all() }.flatten().map { it.data }.find { it.key == dependency }?.state
                        ?: throw IllegalStateException("Dependency $dependency not found. Dependencies must be in the same category and precede the setting")
                    if (state.value !is Boolean) {
                        throw IllegalStateException("A Setting can only depend on Boolean-Settings")
                    }
                    state as MutableLiveData<Boolean>
                }

            }
            val setting = this.compose(
                state = state,
                update = update,
                restorer = restorer,
                enabled = { enabled(it) }
            )
            return (setting as I).let {
                this@Setting.settings.add(it)
                it
            }
        }

        fun menu(menu: String, initializer: M.() -> Unit) {
            this.settings.add(builders.shm(menu, builders.menu.apply(initializer)))
        }
    }

    abstract class Category<
            I : ISetting<Any, I>,
            SS : SingleSetting<Any, I>,
            SHC : SettingCategory<I>,
            SHM : SettingMenu<I>,
            S : Setting<I, SS, SHC, SHM, S, C, M>,
            C : Category<I, SS, SHC, SHM, S, C, M>,
            M : Menu<I, SS, SHC, SHM, S, C, M>
            >(
        private val context: Context,
        private val builders: SettingsBuilders<I, SS, SHC, SHM, S, C, M>,
    ) {
        val settings: MutableList<SettingHierarchy<I>> = mutableListOf()

        @Suppress("Unused", "Unchecked_Cast")
        fun category(category: String, initializer: S.() -> Unit) {
            this.settings.add(builders.shc(category, builders.category.apply { settings.addAll(builders.setting.apply(initializer).settings) }))
        }

        @Suppress("Unused", "Unchecked_Cast")
        fun category(categoryId: Int, initializer: S.() -> Unit) {
            this.settings.add(builders.shc(context.getString(categoryId), builders.category.apply { settings.addAll(builders.setting.apply(initializer).settings) }))
        }
    }

    abstract class Menu<
            I : ISetting<Any, I>,
            SS : SingleSetting<Any, I>,
            SHC : SettingCategory<I>,
            SHM : SettingMenu<I>,
            S : Setting<I, SS, SHC, SHM, S, C, M>,
            C : Category<I, SS, SHC, SHM, S, C, M>,
            M : Menu<I, SS, SHC, SHM, S, C, M>
            >(
        private val builders: SettingsBuilders<I, SS, SHC, SHM, S, C, M>,
    ) {
        var setting: I? = null
            private set
            get() = (field ?: throw IllegalStateException("A menu needs a setting to click on."))
        val settings: MutableList<SettingHierarchy<I>> = mutableListOf()

        fun setting(initializer: S.() -> Unit) {
            this.setting = builders.setting.apply(initializer).settings.single().all().single()
        }

        fun content(initializer: C.() -> Unit) {
            this.settings.addAll(builders.category.apply(initializer).settings)
        }
    }

    companion object {
        @Suppress("Unused")
        fun <
                I : ISetting<Any, I>,
                SS : SingleSetting<Any, I>,
                SHC : SettingCategory<I>,
                SHM : SettingMenu<I>,
                S : Setting<I, SS, SHC, SHM, S, C, M>,
                C : Category<I, SS, SHC, SHM, S, C, M>,
                M : Menu<I, SS, SHC, SHM, S, C, M>,
                Set : Settings<I, SHC, SHM>
                > build(
            builder: (settings: List<SHC>) -> Set,
            builders: SettingsBuilders<I, SS, SHC, SHM, S, C, M>,
            initializer: C.() -> Unit,
        ): Set {
            return builder(builders.category.apply(initializer).settings as List<SHC>)
        }
    }

}