package org.secuso.pfacore.ui.compose.preferences.settings

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import org.secuso.pfacore.model.preferences.settings.ISettingData
import org.secuso.pfacore.model.preferences.settings.ISettingDataBuildInfo
import org.secuso.pfacore.model.preferences.settings.SettingData
import org.secuso.pfacore.model.preferences.settings.SettingDataBuildInfo
import org.secuso.pfacore.model.preferences.settings.SettingEntry
import org.secuso.pfacore.model.preferences.settings.Entries
import org.secuso.pfacore.model.preferences.settings.SettingFactory
import org.secuso.pfacore.ui.compose.BasicInfo
import org.secuso.pfacore.model.preferences.settings.MenuSetting as MMenuSetting
import org.secuso.pfacore.model.preferences.settings.RadioSetting as MRadioSetting
import org.secuso.pfacore.ui.compose.TransformableInfo
import org.secuso.pfacore.model.preferences.settings.SwitchSetting as MSwitchSetting
import org.secuso.pfacore.ui.compose.preferences.settings.composables.MenuPreference
import org.secuso.pfacore.ui.compose.preferences.settings.composables.RadioPreference
import org.secuso.pfacore.ui.compose.preferences.settings.composables.SwitchPreference

open class DisplaySetting<T, SD: ISettingData<T>>(private val resources: Resources) {
    var title: @Composable ((SD, T, Modifier) -> Unit)? = null
    var summary: @Composable ((SD, T, Modifier) -> Unit)? = null
    fun title(initializer: TransformableInfo<SD, T>.() -> Unit) {
        this.title = TransformableInfo<SD, T>(resources) { transformer ->
            { data, value, modifier -> Text(text = transformer(data, value), modifier = modifier) }
        }.apply(initializer).build()
    }
    @Suppress("Unused")
    fun summary(initializer: TransformableInfo<SD, T>.() -> Unit) {
        this.summary = TransformableInfo<SD, T>(resources) { transformer ->
            { data, state, modifier -> Text(text = transformer(data, state), modifier = modifier) }
        }.apply(initializer).build()
    }
    internal fun requireTitle() = title ?: throw IllegalStateException("This setting requires a title")
    internal fun requireSummary() = summary ?: throw IllegalStateException("This setting requires a summary")
}

open class BasicDisplaySetting(private val resources: Resources) {
    var title: @Composable ((Modifier) -> Unit)? = null
    var summary: @Composable ((Modifier) -> Unit)? = null
    fun title(initializer: BasicInfo.() -> Unit) {
        this.title = BasicInfo(resources) { text -> { Text(text = text, modifier = it) } }.apply(initializer).build()
    }
    @Suppress("Unused")
    fun summary(initializer: BasicInfo.() -> Unit) {
        this.summary = BasicInfo(resources) { text -> { Text(text = text, modifier = it) } }.apply(initializer).build()
    }
    internal fun requireTitle() = title ?: throw IllegalStateException("This setting requires a title")
    internal fun requireSummary() = summary ?: throw IllegalStateException("This setting requires a summary")
}


class SwitchSetting(data: SwitchData) : MSwitchSetting<SwitchSetting.SwitchData>(data), DisplayableSettingInfo {
    companion object {
        fun factory(): SettingFactory<Boolean, SwitchBuildInfo, SwitchData> = factory { info, data -> SwitchData(data.data, info.requireTitle(), info.requireSummary()) }
    }

    class SwitchData(
        data: SettingData<Boolean>,
        val title: @Composable (SwitchData, Boolean, Modifier) -> Unit,
        val summary: @Composable (SwitchData, Boolean, Modifier) -> Unit,
    ): MSwitchSetting.SwitchData(data) {
        fun create() = SwitchSetting(this)
    }

    class SwitchBuildInfo(resources: Resources, data: SettingDataBuildInfo<Boolean> = SettingDataBuildInfo()):
        DisplaySetting<Boolean, SwitchData>(resources), MSwitchSetting.SwitchBuildInfo, ISettingDataBuildInfo<Boolean> by data

    @Composable
    override fun Display(
        onClick: (() -> Unit)?
    ) {
        SwitchPreference(
            data,
            data.state.observeAsState(initial = data.default),
            data.enabled.observeAsState(false),
            { data.value = it },
            data.title,
            data.summary,
            onClick
        )
    }
}

class RadioSetting<T>(data: RadioData<T>) : MRadioSetting<T, RadioSetting.RadioData<T>>(data), DisplayableSettingInfo {
    companion object {
        fun <T> factory(): SettingFactory<T, RadioBuildInfo<T>, RadioData<T>> = factory() { info, data -> RadioData(data.data, info.entries, info.requireTitle(), info.requireSummary()) }
    }
    class RadioData<T>(
        data: SettingData<T>,
        entries: List<SettingEntry<T>>,
        val title: @Composable (RadioData<T>, T, Modifier) -> Unit,
        val summary: @Composable (RadioData<T>, T, Modifier) -> Unit,
    ): MRadioSetting.RadioData<T>(data, entries) {
        fun create() = RadioSetting(this)
    }

    class RadioBuildInfo<T>(val resources: Resources, override var entries: List<SettingEntry<T>> = listOf(), data: SettingDataBuildInfo<T> = SettingDataBuildInfo()):
        DisplaySetting<T, RadioData<T>>(resources), MRadioSetting.RadioBuildInfo<T>, ISettingDataBuildInfo<T> by data {
        override fun entries(initializer: Entries<T>.() -> Unit) {
            entries = Entries<T>(resources).apply(initializer).collect()
        }
    }

    @Composable
    override fun Display(
        onClick: (() -> Unit)?
    ) {
        RadioPreference(
            data,
            data.state.observeAsState(initial = data.default!!),
            data.enabled.observeAsState(false),
            { data.value = it },
            data.title,
            data.summary,
            onClick
        )
    }
}

class MenuSetting(data: MenuData) : MMenuSetting<MenuSetting.MenuData>(data), DisplayableSettingInfo {
    companion object {
        fun factory(): SettingFactory<Unit, MenuBuildInfo, MenuData> = factory() { info, data -> MenuData(info.requireTitle(), info.summary) }
    }
    class MenuData(
        val title: @Composable (Modifier) -> Unit,
        val summary: (@Composable (Modifier) -> Unit)?,
    ): MMenuSetting.MenuData() {
        fun create() = MenuSetting(this)
    }
    class MenuBuildInfo(resources: Resources): BasicDisplaySetting(resources), MMenuSetting.MenuBuildInfo
    @SuppressLint("UnrememberedMutableState")
    @Composable
    override fun Display(
        onClick: (() -> Unit)?
    ) {
        MenuPreference(data.title, data.summary, onClick)
    }
}