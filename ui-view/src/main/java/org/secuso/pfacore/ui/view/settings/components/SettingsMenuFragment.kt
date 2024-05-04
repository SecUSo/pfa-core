package org.secuso.pfacore.ui.view.settings.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import org.secuso.pfacore.ui.view.settings.InflatableSetting
import org.secuso.pfacore.ui.view.settings.MenuSetting
import org.secuso.pfacore.ui.view.settings.SettingCategory
import org.secuso.pfacore.ui.view.settings.SettingMenu
import org.secuso.ui.view.R
import org.secuso.ui.view.databinding.FragmentPreferenceMenuBinding
import org.secuso.ui.view.databinding.SettingsMenuCategoryBinding

class SettingsMenuFragment: Fragment() {
    private var binding: FragmentPreferenceMenuBinding? = null
    var categories: List<SettingCategory> = listOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPreferenceMenuBinding.inflate(inflater, container, true)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for (category in categories) {
            val catBinding = SettingsMenuCategoryBinding.inflate(layoutInflater)
            for (setting in category.settings) {
                val setView = (setting as InflatableSetting).inflate(layoutInflater, catBinding.settings, this)
                catBinding.settings.addView(setView)
                if (setting is SettingMenu) {
                    setView.setOnClickListener {
                        val fragment = SettingsMenuFragment().apply { categories = setting.settings as List<SettingCategory> }
                        parentFragmentManager.beginTransaction().replace(R.id.fragment_setting_menu, fragment).addToBackStack(null).commit()
                    }
                }
            }
            catBinding.title = category.name
            binding!!.categories.addView(catBinding.root)
        }
    }
}