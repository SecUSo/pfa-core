package org.secuso.pfacore.ui.compose.settings.composables

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.secuso.pfacore.model.settings.ISetting
import org.secuso.pfacore.model.settings.SettingData
import org.secuso.pfacore.ui.compose.settings.SettingDecorator
import org.secuso.ui.compose.R

val settingModifier = Modifier
    .fillMaxWidth()
    .padding(start = 16.dp, end = 16.dp)

@Composable
fun <T> Preference(
    data: ISetting<T, SettingDecorator<T>>,
    state: State<T>,
    onClick: () -> Unit,
    title: @Composable (SettingData<T>, T, Modifier) -> Unit,
    summary: @Composable (SettingData<T>, T, Modifier) -> Unit,
    action: @Composable () -> Unit,
) {
    Row(
        settingModifier
            .padding(top = 8.dp, bottom = 8.dp)
            .clickable { onClick() }
    ) {
        Column(Modifier.fillMaxWidth(0.8f)) {
            title(data.data, state.value, Modifier)
            summary(data.data, state.value, Modifier)
        }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            action()
        }
    }
}

@Composable
fun SwitchPreference(
    data: ISetting<Boolean, SettingDecorator<Boolean>>,
    enabled: State<Boolean>,
    update: (Boolean) -> Unit,
    title: @Composable (SettingData<Boolean>, Boolean, Modifier) -> Unit,
    summary: @Composable (SettingData<Boolean>, Boolean, Modifier) -> Unit,
    onClick: (() -> Unit)? = null
) {
    val checked = data.data.state.observeAsState()
    Preference(data = data, state = enabled, onClick = {
        if (enabled.value) {
            onClick?.let { it() }
        }
    }, title = title, summary = summary) {
        Switch(checked = checked.value!!, onCheckedChange = update, enabled = enabled.value)
    }
}

@Composable
fun <T> RadioPreference(
    data: ISetting<T, SettingDecorator<T>>,
    enabled: State<Boolean>,
    update: (T) -> Unit,
    title: @Composable (SettingData<T>, T, Modifier) -> Unit,
    summary: @Composable (SettingData<T>, T, Modifier) -> Unit,
    onClick: (() -> Unit)? = null
) {
    val expanded = remember {
        mutableStateOf(false)
    }
    val selected = data.data.state.observeAsState(data.data.defaultValue)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Preference(data = data, state = selected, onClick = {
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
                    items(count = data.data.entries!!.size, key = { data.data.entries!![it].entry }) {
                        val (entry, value) = data.data.entries!![it]
                        Row(settingModifier.clickable { update(value) }, verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = (value == selected.value), onClick = { update(value) })
                            Text(text = entry)
                        }
                    }
                }
            }
        }
    }

}