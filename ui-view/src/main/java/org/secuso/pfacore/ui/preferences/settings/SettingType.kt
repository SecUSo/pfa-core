package org.secuso.pfacore.ui.preferences.settings

import android.content.res.Resources
import android.text.InputType
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LiveData
import org.secuso.pfacore.model.preferences.settings.ISettingData
import org.secuso.pfacore.model.preferences.settings.ISettingDataBuildInfo
import org.secuso.pfacore.model.preferences.settings.SettingData
import org.secuso.pfacore.model.preferences.settings.SettingDataBuildInfo
import org.secuso.pfacore.model.preferences.settings.SettingEntry
import org.secuso.pfacore.model.preferences.settings.Entries
import org.secuso.pfacore.model.preferences.settings.SettingDataFactory
import org.secuso.pfacore.model.preferences.settings.MenuSetting as MMenuSetting
import org.secuso.pfacore.model.preferences.settings.ActionSetting as MActionSetting
import org.secuso.pfacore.model.preferences.settings.RadioSetting as MRadioSetting
import org.secuso.pfacore.model.preferences.settings.InputSetting as MInputSetting
import org.secuso.pfacore.model.preferences.settings.SwitchSetting as MSwitchSetting
import org.secuso.pfacore.model.preferences.settings.TimeSetting as MTimeSetting
import org.secuso.pfacore.ui.BasicInfo
import org.secuso.pfacore.ui.Inflatable
import org.secuso.pfacore.ui.TransformableInfo
import org.secuso.pfacore.ui.preferences.settings.components.RadioAdapter
import org.secuso.ui.view.R
import org.secuso.ui.view.databinding.PreferenceActionListBinding
import org.secuso.ui.view.databinding.PreferenceInputBinding
import org.secuso.ui.view.databinding.PreferenceSwitchBinding
import org.secuso.ui.view.databinding.SimpleDescriptionBinding
import org.secuso.ui.view.databinding.SimpleTitleBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import org.secuso.pfacore.model.preferences.settings.ISettingBehaviour
import org.secuso.pfacore.model.preferences.settings.ISettingBehaviourBuildInfo
import org.secuso.pfacore.model.preferences.settings.SettingBehaviour
import org.secuso.pfacore.model.preferences.settings.SettingBehaviourBuildInfo
import org.secuso.pfacore.model.preferences.settings.SettingFactory
import kotlin.time.Duration.Companion.seconds

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


class SwitchSetting(data: SwitchData) : MSwitchSetting<SwitchSetting.SwitchData>(data), InflatableSettingInfo {
    companion object {
        fun factory(): SettingDataFactory<SwitchBuildInfo, SwitchData> = factory { info, data -> SwitchData(data.data, info.requireTitle(), info.requireSummary()) }
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
                data.state.observe(owner) { action.isChecked = it; Log.d("switch", "state changed $it") }
                enabled = data.enabled.value ?: true
                data.enabled.observe(owner) { enabled = it }
            }.root
        }
}

class RadioSetting<T>(data: RadioData<T>) : MRadioSetting<T, RadioSetting.RadioData<T>>(data), InflatableSettingInfo {
    companion object {
        fun <T> factory(): SettingDataFactory<RadioBuildInfo<T>, RadioData<T>> = factory() { info, data -> RadioData(data.data, info.entries, info.requireTitle(), info.requireSummary()) }
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

class InputSetting<T>(data: InputData<T>) : MInputSetting<T, InputSetting.InputData<T>>(data), InflatableSettingInfo {
    companion object {
        fun <T> factory(): SettingDataFactory<InputBuildInfo<T>, InputData<T>> = factory() { info, data -> InputData(data.data, info.validation,info.requireTitle(), info.requireSummary()) }
    }
    class InputData<T>(
        data: SettingData<T>,
        validation: (value: T?) -> T?,
        val title: (InputData<T>, T) -> Inflatable,
        val summary: (InputData<T>, T) -> Inflatable,
    ): MInputSetting.InputData<T>(data, validation) {
        fun create() = InputSetting(this)
    }

    class InputBuildInfo<T>(val resources: Resources, override var validation: (value: T?) -> T?, data: SettingDataBuildInfo<T> = SettingDataBuildInfo()):
        DisplaySetting<T, InputData<T>>(resources), MInputSetting.InputBuildInfo<T>, ISettingDataBuildInfo<T> by data

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
            PreferenceInputBinding.inflate(inflater, root, false).apply {
                this.action.setText(data.value.toString())
                this.enabled = data.enabled.value ?: true
                this.action.inputType = when (data.default) {
                    is String -> InputType.TYPE_CLASS_TEXT
                    is Int, Long -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
                    is UInt, ULong -> InputType.TYPE_CLASS_NUMBER
                    is Float, is Double -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                    else -> throw UnsupportedOperationException("Preference ${data.key} has a not supported type")
                }
                this.action.doOnTextChanged { text, start, before, count ->
                    val text = text.toString()
                    val value = when (data.default) {
                        is String -> text
                        is Int -> text.toIntOrNull()
                        is Long -> text.toLongOrNull()
                        is UInt -> text.toUIntOrNull()
                        is ULong -> text.toULongOrNull()
                        is Float -> text.toFloatOrNull()
                        is Double -> text.toDoubleOrNull()
                        else -> throw UnsupportedOperationException("Preference ${data.key} has a not supported type")
                    } as T?
                    val afterValidation = data.validation(value)
                    if (afterValidation !== null) {
                        data.value = afterValidation
                    }
                }
            }.root
        }

    override fun onlyRootExpandable() = true
}

class MenuSetting(data: MenuData) : MMenuSetting<MenuSetting.MenuData>(data), InflatableSettingInfo {
    companion object {
        fun factory(): SettingFactory<MenuBuildInfo, MenuData> 
            = factory { info, data -> MenuData(data, info.requireTitle(), info.summary) }
    }
    class MenuData(
        behaviour: ISettingBehaviour,
        val title: Inflatable,
        val summary: Inflatable?,
    ): MMenuSetting.MenuData(behaviour) {
        fun create() = MenuSetting(this)
    }
    class MenuBuildInfo(
        resources: Resources, behaviour: ISettingBehaviourBuildInfo = SettingBehaviourBuildInfo()
    ): BasicDisplaySetting(resources), MMenuSetting.MenuBuildInfo, ISettingBehaviourBuildInfo by behaviour

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

class ActionSetting(data: ActionData) : MActionSetting<ActionSetting.ActionData>(data), InflatableSettingInfo {
    companion object {
        fun factory(): SettingFactory<ActionBuildInfo, ActionData>
            = factory { info, data -> ActionData(data, data.onClick, info.requireTitle(), info.summary) }
    }
    class ActionData(
        behaviour: ISettingBehaviour,
        onClick: (AppCompatActivity) -> Unit,
        val title: Inflatable,
        val summary: Inflatable?,
    ): MActionSetting.ActionData(behaviour, onClick) {
        fun create() = ActionSetting(this)
    }
    class ActionBuildInfo(
        resources: Resources,
        behaviour: ISettingBehaviourBuildInfo = SettingBehaviourBuildInfo(),
        override var onClick: (AppCompatActivity) -> Unit = {}
    ): BasicDisplaySetting(resources), MActionSetting.ActionBuildInfo, ISettingBehaviourBuildInfo by behaviour

    override val expandable: Boolean
        get() = false
    override val title: Inflatable
        get() = data.title
    override val description: Inflatable?
        get() = data.summary
    override fun expandableIcon(expanded: Boolean): Int {
        return 0
    }
    override val onClick: (AppCompatActivity) -> Unit = data.onClick
}

class TimeSetting(data: TimeData) : MTimeSetting<TimeSetting.TimeData>(data), InflatableSettingInfo {
    companion object {
        fun factory(): SettingDataFactory<TimeBuildInfo, TimeData> = factory() { info, data -> TimeData(data.data, info.validation,info.requireTitle(), info.requireSummary()) }
    }
    class TimeData(
        data: SettingData<Long>,
        validation: (hour: Int, minute: Int) -> Boolean,
        val title: (TimeData, Long) -> Inflatable,
        val summary: (TimeData, Long) -> Inflatable,
    ): MTimeSetting.TimeData(data, validation) {
        fun create() = TimeSetting(this)
    }

    class TimeBuildInfo(
        val resources: Resources,
        override var validation: (hour: Int, minute: Int) -> Boolean,
        data: SettingDataBuildInfo<Long> = SettingDataBuildInfo()
    ): DisplaySetting<Long, TimeData>(resources), MTimeSetting.TimeBuildInfo, ISettingDataBuildInfo<Long> by data

    override val enabled: LiveData<Boolean>
        get() = data.enabled
    override val expandable: Boolean
        get() = false
    override val title: Inflatable
        get() = data.title(data, data.state.value ?: data.default)
    override val description: Inflatable
        get() = data.summary(data, data.state.value ?: data.default)
    override fun expandableIcon(expanded: Boolean): Int {
        return 0
    }
    override val onClick: (AppCompatActivity) -> Unit = {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(data.value.seconds.inWholeHours.toInt())
            .setMinute(data.value.seconds.inWholeMinutes.toInt())
            .build()
        picker.addOnPositiveButtonClickListener {
            if (data.validation(picker.hour, picker.minute))
            data.value = (picker.hour * 3600 + picker.minute * 60).toLong()
        }
        picker.show(it.supportFragmentManager, picker.toString())
    }
}