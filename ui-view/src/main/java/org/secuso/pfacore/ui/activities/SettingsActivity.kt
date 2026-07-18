package org.secuso.pfacore.ui.activities

import android.os.Bundle
import org.secuso.pfacore.model.preferences.settings.ISettingBehaviour
import org.secuso.pfacore.model.preferences.settings.ISettingData
import org.secuso.pfacore.model.preferences.settings.Setting
import org.secuso.pfacore.model.preferences.settings.SettingData
import org.secuso.pfacore.ui.PFApplication
import org.secuso.ui.view.R

class SettingsActivity: BaseActivity(base = false) {
    val settingActivityCallbacks: MutableList<() -> Unit> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        val settings = PFApplication.instance.data.preferences.settings
        settingActivityCallbacks.addAll(
            settings
                .all
                .filter { it.setting.data is ISettingBehaviour }
                .mapNotNull { (it.setting.data as ISettingBehaviour).useActivity }
                .mapNotNull { it(this@SettingsActivity) }
                .toList()
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager.beginTransaction().add(R.id.fragment, settings.build(R.id.fragment, supportFragmentManager), null).commit()
    }
}