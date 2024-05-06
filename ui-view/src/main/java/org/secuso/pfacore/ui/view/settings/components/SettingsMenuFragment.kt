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
        binding = FragmentPreferenceMenuBinding.inflate(inflater, container, false).apply {
            settings.adapter = SettingsMenuAdapter(inflater, this@SettingsMenuFragment) {
                SettingsMenuFragment().apply {
                    categories = it.settings as List<SettingCategory>
                    viewId = this@SettingsMenuFragment.viewId
                    parentFragmentManager.beginTransaction().replace(viewId, this).addToBackStack(null).commit()
                }
            }
        }
        return binding!!.root
    }
}