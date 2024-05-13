package org.secuso.pfacore.ui.view.settings

import android.content.res.Resources
import androidx.lifecycle.LiveData
import org.secuso.pfacore.model.ISettingData
import org.secuso.pfacore.model.ISettingDataBuildInfo
import org.secuso.pfacore.model.SettingData
import org.secuso.pfacore.model.SettingDataBuildInfo
import org.secuso.pfacore.model.SettingEntry
import org.secuso.pfacore.model.settings.Entries
import org.secuso.pfacore.model.settings.SettingFactory
import org.secuso.pfacore.model.settings.MenuSetting as MMenuSetting
import org.secuso.pfacore.model.settings.RadioSetting as MRadioSetting
import org.secuso.pfacore.model.settings.SwitchSetting as MSwitchSetting
import org.secuso.pfacore.ui.view.BasicInfo
import org.secuso.pfacore.ui.view.Inflatable
import org.secuso.pfacore.ui.view.TransformableInfo
import org.secuso.pfacore.ui.view.settings.components.RadioAdapter
import org.secuso.ui.view.R
import org.secuso.ui.view.databinding.PreferenceActionListBinding
import org.secuso.ui.view.databinding.PreferenceSwitchBinding
import org.secuso.ui.view.databinding.SimpleDescriptionBinding
import org.secuso.ui.view.databinding.SimpleTitleBinding

open class DisplaySetting<T, SD: ISettingData<T>>(private val resources: Resources) {
    var title: ((SD, T) -> Inflatable)? = null
    var summary: ((SD, T) -> Inflatable)? = null
    fun title(initializer: TransformableInfo<SD, T>.() -> Unit) {
        this.title = TransformableInfo<SD, T>(resources) { transformer ->
            { data, value -> Inflatable { inflater, root, owner ->
                val binding = SimpleTitleBinding.inflate(inflater, root, false)
                binding.text = transformer(data, value)
                data.state.observe(owner) { binding.text = transformer(data, it) }
                binding.root
            } }
        }.apply(initializer).build()
    }
    @Suppress("Unused")
    fun summary(initializer: TransformableInfo<SD, T>.() -> Unit) {
        this.summary = TransformableInfo<SD, T>(resources) { transformer ->
            { data, value -> Inflatable { inflater, root, owner ->
                val binding = SimpleDescriptionBinding.inflate(inflater, root, false)
                binding.text = transformer(data, value)
                data.state.observe(owner) { binding.text = transformer(data, it) }
                binding.root
            } }
        }.apply(initializer).build()
    }
    internal fun requireTitle() = title ?: throw IllegalStateException("This setting requires a title")
    internal fun requireSummary() = summary ?: throw IllegalStateException("This setting requires a summary")
}

open class BasicDisplaySetting(private val resources: Resources) {
    var title: Inflatable? = null
    var summary: Inflatable? = null
    fun title(initializer: BasicInfo.() -> Unit) {
        this.title = BasicInfo(resources) { text -> Inflatable { inflater, root, _ ->
            val binding = SimpleTitleBinding.inflate(inflater, root, false)
            binding.text = text
            binding.root
        } }.apply(initializer).build()
    }
    @Suppress("Unused")
    fun summary(initializer: BasicInfo.() -> Unit) {
        this.summary = BasicInfo(resources) { text -> Inflatable { inflater, root, _ ->
            val binding = SimpleDescriptionBinding.inflate(inflater, root, false)
            binding.text = text
            binding.root
        } }.apply(initializer).build()
    }
    internal fun requireTitle() = title ?: throw IllegalStateException("This setting requires a title")
    internal fun requireSummary() = summary ?: throw IllegalStateException("This setting requires a summary")
}


class SwitchSetting(data: SwitchData) : MSwitchSetting<SwitchSetting.SwitchData>(data), InflatableSetting {
    companion object {
        fun factory(): SettingFactory<Boolean, SwitchBuildInfo, SwitchData> = factory { info, data -> SwitchData(data.data, info.requireTitle(), info.requireSummary()) }
    }

    class SwitchData(
        data: SettingData<Boolean>,
        val title: (SwitchData, Boolean) -> Inflatable,
        val summary: (SwitchData, Boolean) -> Inflatable,
    ): MSwitchSetting.SwitchData(data) {
        fun create() = SwitchSetting(this)
    }

    class SwitchBuildInfo(resources: Resources, data: SettingDataBuildInfo<Boolean> = SettingDataBuildInfo()):
        DisplaySetting<Boolean, SwitchData>(resources), MSwitchSetting.SwitchBuildInfo, ISettingDataBuildInfo<Boolean> by data

    override val enabled: LiveData<Boolean>
        get() = data.enabled
    override val expandable: Boolean
        get() = false
    override val title: Inflatable
        get() = data.title(data, data.state.value ?: data.default)
    override val description: Inflatable
        get() = data.summary(data, data.state.value ?: data.default)
    override val action: Inflatable
        get() = Inflatable { inflater, root, owner ->
            PreferenceSwitchBinding.inflate(inflater, root, false).apply {
                action.setOnClickListener { data.value = !(data.value ?: data.default) }
                action.isChecked = data.value ?: data.default
                data.state.observe(owner) { action.isChecked = it }
                enabled = data.enabled.value ?: true
                data.enabled.observe(owner) { enabled = it }
            }.root
        }
}

class RadioSetting<T>(data: RadioData<T>) : MRadioSetting<T,RadioSetting.RadioData<T>>(data), InflatableSetting {
    companion object {
        fun <T> factory(): SettingFactory<T, RadioBuildInfo<T>, RadioData<T>> = factory() { info, data -> RadioData(data.data, info.entries, info.requireTitle(), info.requireSummary()) }
    }
    class RadioData<T>(
        data: SettingData<T>,
        entries: List<SettingEntry<T>>,
        val title: (RadioData<T>, T) -> Inflatable,
        val summary: (RadioData<T>, T) -> Inflatable,
    ): MRadioSetting.RadioData<T>(data, entries) {
        fun create() = RadioSetting(this)
    }

    class RadioBuildInfo<T>(val resources: Resources, override var entries: List<SettingEntry<T>> = listOf(), data: SettingDataBuildInfo<T> = SettingDataBuildInfo()):
        DisplaySetting<T, RadioData<T>>(resources), MRadioSetting.RadioBuildInfo<T>, ISettingDataBuildInfo<T> by data {
        override fun entries(initializer: Entries<T>.() -> Unit) {
            entries = Entries<T>(resources).apply(initializer).collect()
        }
    }

    override val enabled: LiveData<Boolean>
        get() = data.enabled
    override val expandable: Boolean
        get() = true
    override val title: Inflatable
        get() = data.title(data, data.state.value ?: data.default)
    override val description: Inflatable
        get() = data.summary(data, data.state.value ?: data.default)
    override val action: Inflatable
        get() = Inflatable { inflater, root, _ ->
            PreferenceActionListBinding.inflate(inflater, root, false).apply {
                list.adapter = RadioAdapter(inflater, data.entries, data.value ?: data.default) { data.value = it }
            }.root
        }
}

class MenuSetting(data: MenuData) : MMenuSetting<MenuSetting.MenuData>(data), InflatableSetting {
    companion object {
        fun factory(): SettingFactory<Unit, MenuBuildInfo, MenuData> = factory() { info, data -> MenuData(info.requireTitle(), info.summary) }
    }
    class MenuData(
        val title: Inflatable,
        val summary: Inflatable?,
    ): MMenuSetting.MenuData() {
        fun create() = MenuSetting(this)
    }
    class MenuBuildInfo(resources: Resources): BasicDisplaySetting(resources), MMenuSetting.MenuBuildInfo

    override val expandable: Boolean
        get() = false
    override val title: Inflatable
        get() = data.title
    override val description: Inflatable?
        get() = data.summary
    override fun expandableIcon(expanded: Boolean): Int {
        return R.drawable.baseline_keyboard_arrow_right_24
    }

}