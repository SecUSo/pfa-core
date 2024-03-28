package org.secuso.pfacore.ui.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class SettingsHelper {

    companion object {
        fun <T> useEntryValue(): (@Composable (SettingData<T>, Modifier) -> Unit) {
            return { data, modifier -> Text(text = data.entries!!.find { it.value == data.value }!!.entry, modifier = modifier) }
        }
    }
}