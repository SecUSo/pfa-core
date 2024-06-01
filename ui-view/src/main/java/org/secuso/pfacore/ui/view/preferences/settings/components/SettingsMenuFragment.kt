package org.secuso.pfacore.ui.view.preferences.settings.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.secuso.pfacore.ui.view.preferences.settings.SettingCategory
import org.secuso.ui.view.databinding.FragmentPreferenceMenuBinding

class SettingsMenuFragment: Fragment() {
    private var binding: FragmentPreferenceMenuBinding? = null
    var openMenu: ((SettingsMenuFragment) -> Unit)? = null
    var categories: List<SettingCategory> = listOf()
        set(value) {
            if (binding != null && binding!!.settings.adapter != null) {
                (binding!!.settings.adapter!! as SettingsMenuAdapter).apply { items = value }
            }
            field = value
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPreferenceMenuBinding.inflate(inflater, container, false).apply {
            settings.adapter = SettingsMenuAdapter(inflater, this@SettingsMenuFragment) {
                SettingsMenuFragment().apply {
                    categories = it.settings
                    openMenu = this@SettingsMenuFragment.openMenu
                    openMenu!!(this)
                }
            }.apply { items = categories }
        }
        return binding!!.root
    }
}