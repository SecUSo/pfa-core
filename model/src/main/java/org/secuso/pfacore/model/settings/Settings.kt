package org.secuso.pfacore.model.settings

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import org.secuso.pfacore.backup.Restorer
import org.secuso.pfacore.backup.booleanRestorer
import org.secuso.pfacore.backup.doubleRestorer
import org.secuso.pfacore.backup.floatRestorer
import org.secuso.pfacore.backup.intRestorer
import org.secuso.pfacore.backup.noRestorer
import org.secuso.pfacore.backup.stringRestorer
import org.secuso.pfacore.model.DataSaverUpdater
import org.secuso.pfacore.model.EnabledByDependency
import org.secuso.pfacore.model.ISettingData
import org.secuso.pfacore.model.Setting
import org.secuso.pfacore.model.SettingBuildInfo
import org.secuso.pfacore.model.SettingInfo

interface ISettings<SI: SettingInfo> {
    @Suppress("unused")
    val all: List<SettingComposite<SI, *>>
}

abstract class Settings<SI: SettingInfo, SHC : SettingCategory<SI>, SHM : SettingMenu<SI>>(internal val settings: List<SHC>) : ISettings<SI> {
    override val all
        get() = settings.map { it.allSettings() }.flatten()

    data class SettingsBuilders<
            SI: SettingInfo,
            SHC : SettingCategory<SI>,
            SHM : SettingMenu<SI>,
            S : Setting<SI, SHC, SHM, S, C, M>,
            C : Category<SI, SHC, SHM, S, C, M>,
            M : Menu<SI, SHC, SHM, S, C, M>,
            >(
        private val _setting: (SettingsBuilders<SI, SHC, SHM, S, C, M>) -> S,
        private val _category: (SettingsBuilders<SI, SHC, SHM, S, C, M>) -> C,
        private val _menu: (SettingsBuilders<SI, SHC, SHM, S, C, M>) -> M,
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
            SI: SettingInfo,
            SHC : SettingCategory<SI>,
            SHM : SettingMenu<SI>,
            S : Setting<SI, SHC, SHM, S, C, M>,
            C : Category<SI, SHC, SHM, S, C, M>,
            M : Menu<SI, SHC, SHM, S, C, M>
            >(
        val preferences: SharedPreferences,
        val settings: MutableList<SettingHierarchy<SI>> = mutableListOf(),
        val builders: SettingsBuilders<SI, SHC, SHM, S, C, M>
    ) {
        protected val enabled: EnabledByDependency = { dependency ->
            if (dependency == null) {
                MutableLiveData(true)
            } else {
                val state = settings.filterIsInstance<ISettingData<Any>>().find { it.key == dependency }?.state
                    ?: throw IllegalStateException("Dependency $dependency not found. Dependencies must be in the same category and precede the setting")
                state.map {
                    if (it !is Boolean) {
                        throw IllegalStateException("A Setting can currently only depend on Boolean-Settings")
                    }
                    it
                }
            }
        }

        protected fun <T, BI: SettingBuildInfo, SI: SettingInfo> BI.build(factory: SettingFactory<T, BI, SI>): SI {
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
            return factory(state, enabled, restorer, update).build(this).invoke()
        }
        protected fun <S: org.secuso.pfacore.model.Setting<*>> S.register(): S = this.apply { settings.add(SettingComposite(this as org.secuso.pfacore.model.Setting<SI>)) }

        fun menu(menu: String, initializer: M.() -> Unit) {
            this.settings.add(builders.shm(menu, builders.menu.apply(initializer)))
        }
    }

    abstract class Category<
            SI: SettingInfo,
            SHC : SettingCategory<SI>,
            SHM : SettingMenu<SI>,
            S : Setting<SI, SHC, SHM, S, C, M>,
            C : Category<SI, SHC, SHM, S, C, M>,
            M : Menu<SI, SHC, SHM, S, C, M>
            >(
        private val context: Context,
        private val builders: SettingsBuilders<SI, SHC, SHM, S, C, M>,
    ) {
        val settings: MutableList<SettingHierarchy<SI>> = mutableListOf()

        @Suppress("Unused")
        fun category(category: String, initializer: S.() -> Unit) {
            this.settings.add(builders.shc(category, builders.category.apply { settings.addAll(builders.setting.apply(initializer).settings) }))
        }

        @Suppress("Unused")
        fun category(categoryId: Int, initializer: S.() -> Unit) {
            this.settings.add(builders.shc(context.getString(categoryId), builders.category.apply { settings.addAll(builders.setting.apply(initializer).settings) }))
        }
    }

    abstract class Menu<
            SI: SettingInfo,
            SHC : SettingCategory<SI>,
            SHM : SettingMenu<SI>,
            S : Setting<SI, SHC, SHM, S, C, M>,
            C : Category<SI, SHC, SHM, S, C, M>,
            M : Menu<SI, SHC, SHM, S, C, M>
            >(
        private val builders: SettingsBuilders<SI, SHC, SHM, S, C, M>,
    ) {
        var setting: SettingComposite<SI, *>? = null
            private set
            get() = (field ?: throw IllegalStateException("A menu needs a setting to click on."))
        val settings: MutableList<SettingHierarchy<SI>> = mutableListOf()

        fun setting(initializer: S.() -> Unit) {
            this.setting = builders.setting.apply(initializer).settings.single().all().single() as SettingComposite<SI, *>
        }

        fun content(initializer: C.() -> Unit) {
            this.settings.addAll(builders.category.apply(initializer).settings)
        }
    }

    companion object {
        @Suppress("Unused")
        fun <
                SI: SettingInfo,
                SHC : SettingCategory<SI>,
                SHM : SettingMenu<SI>,
                S : Setting<SI, SHC, SHM, S, C, M>,
                C : Category<SI, SHC, SHM, S, C, M>,
                M : Menu<SI, SHC, SHM, S, C, M>,
                Set : Settings<SI, SHC, SHM>
                > build(
            builder: (settings: List<SHC>) -> Set,
            builders: SettingsBuilders<SI, SHC, SHM, S, C, M>,
            initializer: C.() -> Unit,
        ): Set {
            return builder(builders.category.apply(initializer).settings as List<SHC>)
        }
    }

}