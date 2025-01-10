package org.secuso.pfacore.model.preferences.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.preference.PreferenceManager
import org.secuso.pfacore.model.preferences.BuildInfo
import org.secuso.pfacore.model.preferences.Info
import org.secuso.pfacore.model.preferences.PreferenceDSL

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
@PreferenceDSL
abstract class Settings<SI: Info>(internal val settings: List<SettingCategory<SI>>) : ISettings<SI> {
    override val all
        get() = settings.map { it.allSettings() }.flatten()

    @PreferenceDSL
    class Setting<SI: Info>(
        val context: Context,
        private val allSettings: () -> List<CategoricalSettingHierarchy<SI>>,
        private val addSetting: (CategoricalSettingHierarchy<SI>) -> Unit,
    ) {
        val enabled: EnabledByDependency = { dependencies ->
            if (dependencies.dependencies.isEmpty()) {
                MutableLiveData(true)
            } else {

                val dependencies = allSettings()
                    .map { it.setting().data }
                    .filterIsInstance<ISettingData<Any>>()
                    .mapNotNull { setting ->
                        when(val dependency = dependencies.dependencies.find { (key, _) -> setting.key == key }) {
                            null -> null
                            else -> setting to dependency.second
                        }
                    }
                // Use a mediator to react on all dependencies
                val mediatorLiveData: MediatorLiveData<Boolean> = MediatorLiveData()
                mediatorLiveData.value = dependencies
                    .map { (setting, condition) -> condition(setting.value) }
                    .all { condition -> condition == true }
                // add every dependencies state to the mediator
                // the emitted value is only true iff all conditions are true
                dependencies.forEach { (setting, _) ->
                    mediatorLiveData.addSource(setting.state) {
                        mediatorLiveData.value = dependencies
                            .map { (setting, condition) -> condition(setting.value) }
                            .all { condition -> condition == true }
                    }
                }
                mediatorLiveData
            }
        }

        fun <BI: BuildInfo, SI: Info> BI.build(factory: SettingFactory<BI, SI>): SI {
            return factory(PreferenceManager.getDefaultSharedPreferences(context), enabled).build(this).invoke()
        }
        fun <S: org.secuso.pfacore.model.preferences.settings.Setting<*>> S.register(): S = this.apply { addSetting(SettingComposite(this as org.secuso.pfacore.model.preferences.settings.Setting<SI>)) }

        fun menu(menu: String, initializer: Menu<SI>.() -> Unit) {
            val settingMenu = Menu(context, allSettings).apply(initializer)
            addSetting(SettingMenu(menu, settingMenu.setting!!, settingMenu.settings))
        }
    }

    @PreferenceDSL
    class Category<SI: Info>(
        private val context: Context,
    ) {
        val categories: MutableList<SettingCategory<SI>> = mutableListOf()
        val allSettings: MutableList<CategoricalSettingHierarchy<SI>> = mutableListOf()

        @Suppress("Unused")
        fun category(category: String, initializer: Setting<SI>.() -> Unit) {
            val settings = mutableListOf<CategoricalSettingHierarchy<SI>>()
            Setting(context, { allSettings }) {
                settings.add(it)
                allSettings.add(it)
            }.apply(initializer)
            categories.add(SettingCategory(category, settings))
        }

        @Suppress("Unused")
        fun category(categoryId: Int, initializer: Setting<SI>.() -> Unit) {
            category(context.getString(categoryId), initializer)
        }
    }

    @PreferenceDSL
    class Menu<SI: Info, >(
        private val context: Context,
        private val allSettings: () -> List<CategoricalSettingHierarchy<SI>>,
    ) {
        var setting: SettingComposite<SI, *>? = null
            private set
            get() = (field ?: throw IllegalStateException("A menu needs a setting to click on."))
        val settings: MutableList<SettingCategory<SI>> = mutableListOf()

        fun setting(initializer: Setting<SI>.() -> Unit) {
            val settings = mutableListOf<CategoricalSettingHierarchy<SI>>()
            Setting(context, allSettings) {
                settings.add(it)
            }.apply(initializer)
        }

        fun content(initializer: Category<SI>.() -> Unit) {
            this.settings.addAll(Category<SI>(context).apply(initializer).categories)
        }
    }

    companion object {
        @Suppress("Unused")
        fun <SI: Info, Set : Settings<SI>> build(
            context: Context,
            builder: (settings: List<SettingCategory<SI>>) -> Set,
            initializer: Category<SI>.() -> Unit,
        ): Set {
            return builder(Category<SI>(context).apply(initializer).categories)
        }
    }

}