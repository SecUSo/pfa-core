package org.secuso.privacyfriendlycore.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class SettingData<T>(
    var key: String,
    var state: MutableState<T>,
    var defaultValue: T,
    var title: @Composable (T, modifier: Modifier) -> Unit,
    var summary: @Composable (T, modifier: Modifier) -> Unit,
    private var _composable: @Composable (SettingData<T>) -> Unit
) {
    val composable = @Composable { this._composable(this) }
    val value = state.value
}

@Composable
fun <T> Preference(
    data: SettingData<T>,
    onClick: () -> Unit,
    action: @Composable () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            .clickable { onClick() }
    ) {
        Column(Modifier.fillMaxWidth(0.8f)) {
            data.title(data.value, Modifier)
            data.summary(data.value, Modifier)
        }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            action()
        }
    }
}

@Composable
fun SwitchPreference(
    data: SettingData<Boolean>,
    checked: State<Boolean>,
    update: (Boolean) -> Unit
) {
    Preference(data = data, onClick = { update(!checked.value) }) {
        Switch(checked = checked.value, onCheckedChange = update)
    }
}