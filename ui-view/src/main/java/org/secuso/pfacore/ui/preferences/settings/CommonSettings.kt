package org.secuso.pfacore.ui.preferences.settings

import org.secuso.pfacore.R
import org.secuso.pfacore.model.Theme
import org.secuso.pfacore.model.preferences.Preferable
import org.secuso.pfacore.model.preferences.Preferences

class SettingThemeSelector {
    companion object {
        const val themeSelectorKey = "settings_day_night_theme"
    }

    fun build(): InflatableSetting.() -> RadioSetting.RadioData<String> {
        return {
            radio<String> {
                key = themeSelectorKey
                default = Theme.SYSTEM.toString()
                onUpdate = { Theme.valueOf(it).apply() }
                title { resource(R.string.select_day_night_theme) }
                summary { transform { state, value -> state.entries.find { it.value == value }!!.entry } }
                entries {
                    entries(R.array.array_day_night_theme)
                    values(Theme.entries.map { it.toString() })
                }
            }
        }
    }
}
val InflatableSetting.settingThemeSelector
    get() = SettingThemeSelector().build().invoke(this)

class PreferenceFirstTimeLaunch {
    companion object {
        const val firstTimeLaunchKey = "IsFirstTimeLaunch"
    }
    fun build(): Preferences.Preference.() -> Preferable<Boolean> {
        return {
            preference {
                key = firstTimeLaunchKey
                default = true
                backup = true
            }
        }
    }
}
val Preferences.Preference.preferenceFirstTimeLaunch
    get() = PreferenceFirstTimeLaunch().build().invoke(this)

class DeviceInformationOnErrorReport {
    companion object {
        const val includeDeviceDataInReportKey = "includeDeviceDataInReport"
    }
    fun build(): InflatableSetting.() -> SwitchSetting.SwitchData {
        return {
            switch {
                key = includeDeviceDataInReportKey
                default = true
                backup = true
                title { resource(R.string.include_device_info_error_title) }
                summary { resource(R.string.include_device_info_error_summary) }
            }
        }
    }
}
val InflatableSetting.settingDeviceInformationOnErrorReport
    get() = DeviceInformationOnErrorReport().build().invoke(this)