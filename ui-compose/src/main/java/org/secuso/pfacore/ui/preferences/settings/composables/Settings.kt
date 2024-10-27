package org.secuso.pfacore.ui.preferences.settings.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.secuso.pfacore.model.preferences.settings.ISettingData
import org.secuso.pfacore.ui.preferences.settings.RadioSetting
import org.secuso.pfacore.ui.preferences.settings.SwitchSetting
import org.secuso.ui.compose.R

val settingModifier = Modifier
    .fillMaxWidth()

@Composable
fun PreferenceLayout(
    title: @Composable (Modifier) -> Unit,
    summary: (@Composable (Modifier) -> Unit)?,
    onClick: () -> Unit,
    action: @Composable () -> Unit,
) {
    Row(
        settingModifier
            .padding(top = 8.dp, bottom = 8.dp)
            .clickable { onClick() }
    ) {
        Column(Modifier.fillMaxWidth(0.8f)) {
            title(Modifier)
            summary?.let { it(Modifier) }
        }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            action()
        }
    }
}

@Composable
fun <T, SD: ISettingData<T>> Preference(
    data: SD,
    state: State<T>,
    onClick: () -> Unit,
    title: @Composable (SD, T, Modifier) -> Unit,
    summary: @Composable (SD, T, Modifier) -> Unit,
    action: @Composable () -> Unit,
) {
    PreferenceLayout(
        title = { title(data, state.value, it) },
        summary = { summary(data, state.value, it) },
        onClick = onClick
    ) { action() }
}

@Composable
fun SwitchPreference(
    data: SwitchSetting.SwitchData,
    state: State<Boolean>,
    enabled: State<Boolean>,
    update: (Boolean) -> Unit,
    title: @Composable (SwitchSetting.SwitchData, Boolean, Modifier) -> Unit,
    summary: @Composable (SwitchSetting.SwitchData, Boolean, Modifier) -> Unit,
    onClick: (() -> Unit)? = null
) {
    Preference(data = data, state = state, onClick = {
        if (enabled.value) {
            onClick?.let { it() }
        }
    }, title = title, summary = summary) {
        Switch(checked = state.value, onCheckedChange = update, enabled = enabled.value)
    }
}

@Composable
fun <T> RadioPreference(
    data: RadioSetting.RadioData<T>,
    state: State<T>,
    enabled: State<Boolean>,
    update: (T) -> Unit,
    title: @Composable (RadioSetting.RadioData<T>, T, Modifier) -> Unit,
    summary: @Composable (RadioSetting.RadioData<T>, T, Modifier) -> Unit,
    onClick: (() -> Unit)? = null
) {
    val expanded = remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Preference(data = data, state = state, onClick = {
            if (enabled.value) {
                onClick?.let { it() }
            }
        }, title = title, summary = summary) {
            IconToggleButton(checked = expanded.value, onCheckedChange = {
                if (enabled.value) {
                    expanded.value = !expanded.value
                }
            }) {
                if (!expanded.value) {
                    Icon(painter = painterResource(id = R.drawable.baseline_expand_more_24), contentDescription = "Expand")
                } else {
                    Icon(painter = painterResource(id = R.drawable.baseline_expand_less_24), contentDescription = "Collapse")
                }
            }
        }
        if (expanded.value) {
            Card(modifier = settingModifier) {
                LazyColumn(Modifier.heightIn(max = 256.dp)) {
                    items(count = data.entries.size, key = { data.entries[it].entry }) {
                        val (entry, value) = data.entries[it]
                        Row(settingModifier.clickable { update(value) }, verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = (value == state.value), onClick = { update(value) })
                            Text(text = entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuPreference(
    title: @Composable (Modifier) -> Unit,
    summary: (@Composable (Modifier) -> Unit)?,
    onClick: (() -> Unit)? = null
) {
    PreferenceLayout(title = title, summary = summary, onClick = { onClick?.let { it() } }) {
        Icon(imageVector = Icons.Filled.KeyboardArrowRight, contentDescription = "")
    }
}