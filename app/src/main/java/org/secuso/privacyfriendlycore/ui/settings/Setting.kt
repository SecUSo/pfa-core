package org.secuso.privacyfriendlycore.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.secuso.privacyfriendlycore.R

data class SettingEntry<T>(
    var entry: String,
    var value: T
)
data class SettingData<T>(
    var key: String,
    var state: MutableState<T>,
    var defaultValue: T,
    var title: @Composable (SettingData<T>, T, modifier: Modifier) -> Unit,
    var summary: @Composable (SettingData<T>, T, modifier: Modifier) -> Unit,
    private var _composable: @Composable (SettingData<T>) -> Unit,
    var entries: List<SettingEntry<T>>? = null,
    var enable: State<Boolean>
) {
    val composable = @Composable { this._composable(this) }
    val value = state.value
}

val settingModifier = Modifier
    .fillMaxWidth()
    .padding(start = 16.dp, end = 16.dp)

@Composable
fun <T> Preference(
    data: SettingData<T>,
    state: State<T>,
    onClick: () -> Unit,
    action: @Composable () -> Unit
) {
    Row(
        settingModifier
            .padding(top = 8.dp, bottom = 8.dp)
            .clickable { onClick() }
    ) {
        Column(Modifier.fillMaxWidth(0.8f)) {
            data.title(data, state.value, Modifier)
            data.summary(data, state.value, Modifier)
        }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            action()
        }
    }
}

@Composable
fun SwitchPreference(
    data: SettingData<Boolean>,
    enabled: State<Boolean>,
    update: (Boolean) -> Unit
) {
    val checked by remember {
        derivedStateOf { data.state }
    }
    Preference(data = data, state = enabled, onClick = { if (enabled.value) update(!checked.value) }) {
        Switch(checked = checked.value, onCheckedChange = update, enabled = enabled.value)
    }
}

@Composable
fun <T> RadioPreference(
    data: SettingData<T>,
    enabled: State<Boolean>,
    update: (T) -> Unit
) {
    val expanded = remember {
        mutableStateOf(false)
    }
    val selected by remember {
        derivedStateOf { data.state }
    }
    Preference(data = data, state = selected, onClick = { if (enabled.value) {expanded.value = !expanded.value}}) {
        IconToggleButton(checked = expanded.value, onCheckedChange = {  if (enabled.value) {
            expanded.value = !expanded.value
        }}) {
            if (!expanded.value) {
                Icon(painter = painterResource(id = R.drawable.baseline_expand_more_24), contentDescription = "Expand")
            } else {
                Icon(painter = painterResource(id = R.drawable.baseline_expand_less_24), contentDescription = "Collapse")
            }
        }
    }
    if (expanded.value) {
        Card(modifier = settingModifier) {
            Column {
                data.entries!!.forEach { (entry, value) ->
                    Row(settingModifier.clickable { update(value) }, verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = (value == selected.value), onClick = { update(value) })
                        Text(text = entry)
                    }
                }
            }
        }
    }

}