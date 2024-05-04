package org.secuso.pfacore.ui.view.settings.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.secuso.pfacore.model.settings.SettingComposite
import org.secuso.pfacore.ui.view.settings.InflatableSetting
import org.secuso.pfacore.ui.view.settings.SettingCategory
import org.secuso.pfacore.ui.view.settings.SettingMenu
import org.secuso.ui.view.databinding.FragmentPreferenceMenuBinding
import org.secuso.ui.view.databinding.SettingsMenuCategoryBinding

class SettingsMenuFragment: Fragment() {
    private var binding: FragmentPreferenceMenuBinding? = null
    var categories: List<SettingCategory> = listOf()
    var viewId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentPreferenceMenuBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for (category in categories) {
            val catBinding = SettingsMenuCategoryBinding.inflate(layoutInflater)
            for (setting in category.settings) {
                when (setting) {
                    is SettingComposite -> {
                        catBinding.settings.addView((setting as InflatableSetting).inflate(layoutInflater, catBinding.settings, this))
                    }
                    is SettingMenu -> {
                        val setView = (setting.menu.setting as InflatableSetting).inflate(layoutInflater, catBinding.settings, this)
                        setView.setOnClickListener {
                            val fragment = SettingsMenuFragment().apply { categories = setting.settings as List<SettingCategory> }
                            parentFragmentManager.beginTransaction().replace(viewId, fragment).addToBackStack(null).commit()
                        }
                    }
                    is SettingCategory -> throw IllegalStateException("Category ${category.name} cannot contain another Category ${setting.name}")
                }
            }
            catBinding.title = category.name
            binding!!.categories.addView(catBinding.root)
        }
    }
}