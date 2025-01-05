package org.secuso.pfacore.model.preferences.settings

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
import org.secuso.pfacore.model.preferences.PreferenceFactory
import org.secuso.pfacore.model.preferences.BuildInfo
import org.secuso.pfacore.model.preferences.DataSaverUpdater
import org.secuso.pfacore.model.preferences.Info
import org.secuso.pfacore.model.preferences.InfoFactory

interface ISettings<SI: Info> {
    @Suppress("unused")
    val all: List<SettingComposite<SI, *>>
}

/**
 * The base class containing all [Settings][org.secuso.pfacore.model.preferences.settings.Setting].
 * It is intended to provide the structure to add, observe, store and link together settings whilst being open enough to provide own, concrete implementations of the settings.
 *
 * Using it's DSL it is possible to declare the settings declarative.
 * Intended Usage after specialization:
 *
 *          // Note that this does not compile as the [Settings.build] method needs builders for the concrete setting types
 *          Settings.build() {
 *              // appearance is also an extension to directly build an appearance section.
 *              appearance {
 *                  SettingThemeSelector().build().invoke(this)
 *              }
 *              category("Error Report") {
 *                  // also an extension
 *                  includeDeviceDataInReport = deviceInformationOnErrorReport
 *              }
 *          }
 *
 * @author Patrick Schneider
 */
abstract class Settings<SI: Info, SHC : SettingCategory<SI>, SHM : SettingMenu<SI, SHC>>(internal val settings: List<SHC>) : ISettings<SI> {
    override val all
        get() = settings.map { it.allSettings() }.flatten()

    data class SettingsBuilders<
            SI: Info,
            SHC : SettingCategory<SI>,
            SHM : SettingMenu<SI, SHC>,
            S : Setting<SI, SHC, SHM, S, C, M>,
            C : Category<SI, SHC, SHM, S, C, M>,
            M : Menu<SI, SHC, SHM, S, C, M>,
            >(
        private val _setting: (SettingsBuilders<SI, SHC, SHM, S, C, M>) -> S,
        private val _category: (SettingsBuilders<SI, SHC, SHM, S, C, M>) -> C,
        private val _menu: (SettingsBuilders<SI, SHC, SHM, S, C, M>) -> M,
        val shc: (String, S) -> SHC,
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
            SI: Info,
            SHC : SettingCategory<SI>,
            SHM : SettingMenu<SI, SHC>,
            S : Setting<SI, SHC, SHM, S, C, M>,
            C : Category<SI, SHC, SHM, S, C, M>,
            M : Menu<SI, SHC, SHM, S, C, M>
            >(
        val preferences: SharedPreferences,
        val settings: MutableList<CategoricalSettingHierarchy<SI>> = mutableListOf(),
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

        protected fun <BI: BuildInfo, SI: Info> BI.build(factory: SettingFactory<BI, SI>): SI {
            return factory(preferences, enabled).build(this).invoke()
        }
        protected fun <S: org.secuso.pfacore.model.preferences.settings.Setting<*>> S.register(): S = this.apply { settings.add(SettingComposite(this as org.secuso.pfacore.model.preferences.settings.Setting<SI>)) }

        fun menu(menu: String, initializer: M.() -> Unit) {
            this.settings.add(builders.shm(menu, builders.menu.apply(initializer)))
        }
    }

    open class Category<
            SI: Info,
            SHC : SettingCategory<SI>,
            SHM : SettingMenu<SI, SHC>,
            S : Setting<SI, SHC, SHM, S, C, M>,
            C : Category<SI, SHC, SHM, S, C, M>,
            M : Menu<SI, SHC, SHM, S, C, M>
            >(
        private val context: Context,
        private val builders: SettingsBuilders<SI, SHC, SHM, S, C, M>,
    ) {
        val categories: MutableList<SHC> = mutableListOf()

        @Suppress("Unused")
        fun category(category: String, initializer: S.() -> Unit) {
            this.categories.add(builders.shc(category, builders.setting.apply(initializer)))
        }

        @Suppress("Unused")
        fun category(categoryId: Int, initializer: S.() -> Unit) {
            this.categories.add(builders.shc(context.getString(categoryId), builders.setting.apply(initializer)))
        }
    }

    open class Menu<
            SI: Info,
            SHC : SettingCategory<SI>,
            SHM : SettingMenu<SI, SHC>,
            S : Setting<SI, SHC, SHM, S, C, M>,
            C : Category<SI, SHC, SHM, S, C, M>,
            M : Menu<SI, SHC, SHM, S, C, M>
            >(
        private val builders: SettingsBuilders<SI, SHC, SHM, S, C, M>,
    ) {
        var setting: SettingComposite<SI, *>? = null
            private set
            get() = (field ?: throw IllegalStateException("A menu needs a setting to click on."))
        val settings: MutableList<SHC> = mutableListOf()

        fun setting(initializer: S.() -> Unit) {
            this.setting = builders.setting.apply(initializer).settings.single().allSettings().single()
        }

        fun content(initializer: C.() -> Unit) {
            this.settings.addAll(builders.category.apply(initializer).categories)
        }
    }

    companion object {
        @Suppress("Unused")
        fun <
                SI: Info,
                SHC : SettingCategory<SI>,
                SHM : SettingMenu<SI, SHC>,
                S : Setting<SI, SHC, SHM, S, C, M>,
                C : Category<SI, SHC, SHM, S, C, M>,
                M : Menu<SI, SHC, SHM, S, C, M>,
                Set : Settings<SI, SHC, SHM>
                > build(
            builder: (settings: List<SHC>) -> Set,
            builders: SettingsBuilders<SI, SHC, SHM, S, C, M>,
            initializer: C.() -> Unit,
        ): Set {
            return builder(builders.category.apply(initializer).categories)
        }
    }

}