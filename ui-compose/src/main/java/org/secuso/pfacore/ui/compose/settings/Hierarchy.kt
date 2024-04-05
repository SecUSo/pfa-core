package org.secuso.pfacore.ui.compose.settings

import android.util.JsonReader
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.secuso.pfacore.model.settings.ISetting
import org.secuso.pfacore.model.settings.SettingCategory
import org.secuso.pfacore.model.settings.SettingData
import org.secuso.pfacore.model.settings.SettingMenu
import org.secuso.pfacore.ui.compose.Displayable

interface DisplayableInnerSetting<T, S : ISetting<T, S>> : ISetting<T, S> {
    @Composable
    fun Display(
        title: @Composable (SettingData<T>, T, Modifier) -> Unit,
        summary: @Composable (SettingData<T>, T, Modifier) -> Unit,
        onClick: (() -> Unit)?
    )
}

class SettingDecorator<T>(
    val setting: DisplayableInnerSetting<T, SettingDecorator<T>>,
    val title: @Composable (SettingData<T>, T, Modifier) -> Unit,
    val summary: @Composable (SettingData<T>, T, Modifier) -> Unit,
) : Displayable, ISetting<T, SettingDecorator<T>> {

    override fun all() = listOf(this)
    override val data: SettingData<T>
        get() = setting.data

    override val backup: Boolean
        get() = setting.backup

    override fun restore(reader: JsonReader) {
        setting.restore(reader)
    }

    @Composable
    override fun Display(onClick: (() -> Unit)?) = setting.Display(title, summary, onClick)
}

class SettingCategory(name: String, private val category: Settings.Category) : SettingCategory<SettingDecorator<Any>>(name, category.settings)
class SettingMenu(name: String, val menu: Settings.Menu) : SettingMenu<SettingDecorator<Any>>(name, menu.settings)