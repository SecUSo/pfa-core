package org.secuso.pfacore.model.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

data class SettingEntry<T>(
    var entry: String,
    var value: T
)

data class SettingData<T>(
    var key: String,
    var state: MutableLiveData<T>,
    var defaultValue: T,
    var entries: List<SettingEntry<T>>? = null,
    var enable: LiveData<Boolean>,
    val onUpdate: (T) -> Unit
) {
    var value = state.value
        set(value) {
            state.value = value!!
            onUpdate(value)
        }
}