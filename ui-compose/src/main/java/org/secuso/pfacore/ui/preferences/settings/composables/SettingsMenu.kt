package org.secuso.pfacore.ui.preferences.settings.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.secuso.pfacore.model.preferences.settings.SettingCategory
import org.secuso.pfacore.model.preferences.settings.SettingMenu
import org.secuso.pfacore.ui.preferences.settings.DisplayableSettingInfo
import org.secuso.pfacore.ui.preferences.appPreferences
import org.secuso.pfacore.ui.preferences.settings.menu
import org.secuso.pfacore.ui.preferences.settings.radio
import org.secuso.pfacore.ui.preferences.settings.switch
import org.secuso.pfacore.ui.theme.PrivacyFriendlyCoreTheme

@Composable
fun PreferenceGroupHeader(modifier: Modifier = Modifier, text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        style = MaterialTheme.typography.titleSmall
    )
}

@Composable
fun SettingsMenu(settings: List<SettingCategory<DisplayableSettingInfo>>) {
    val navController = rememberNavController()
    val menus = remember {
        settings.map { it.settings }.flatten().filterIsInstance<SettingMenu<DisplayableSettingInfo, SettingCategory<DisplayableSettingInfo>>>()
    }
    NavHost(navController = navController, startDestination = "menu") {
        composable("menu") {
            LazyColumn(Modifier.fillMaxWidth().padding(16.dp)) {
                items(count = settings.size) {
                    val category = settings[it]
                    PreferenceGroupHeader(text = category.name)
                    category.settings
                        .map { setting ->
                            Pair(setting.setting(), when(setting) {
                                is SettingMenu<*,*> -> ({ navController.navigate("_${setting.name}") })
                                else -> ({})
                            })
                        }
                        .filterIsInstance<Pair<DisplayableSettingInfo, () -> Unit>>()
                        .forEach { (setting, onClick) -> setting.Display(onClick) }
                }
            }
        }
        for (menu in menus) {
            composable("_${menu.name}") {
                SettingsMenu(settings = menu.settings)
            }
        }
    }
}

@Preview
@Composable
fun SettingsMenuPreview() {
    val context = LocalContext.current
    val preferences = appPreferences(LocalContext.current) {
        preferences {
            preference<String> {
                key = "test"
                default = ""
                backup = false
            }
        }
        settings {
            category("General") {
                switch {
                    key = "test5"
                    default = false
                    backup = false
                    title { literal("Test setting") }
                    summary { literal("Test summary") }
                }
                radio<Int> {
                    key = "test2"
                    default = 1
                    backup = true
                    title { literal("Test radio setting with int") }
                    summary { literal("Test summary") }
                    entries {
                        entries(listOf("A", "B", "C"))
                        values(listOf(1, 2, 3))
                    }
                    dependency = {
                        "test5" on { it == true }
                    }
                }
                menu("Next Menu") {
                    setting {
                        switch {
                            key = "test5"
                            default = false
                            backup = false
                            title { literal("Test Menu") }
                            summary { literal("Click Me") }
                        }
                    }
                    content {
                        category("First Category") {
                            radio {
                                key = "test3"
                                default = "X"
                                backup = true
                                title { literal("Test radio setting") }
                                summary { literal("Test summary") }
                                entries {
                                    entries(listOf("A", "B", "C"))
                                    values(listOf("Z", "Y", "X"))
                                }
                            }
                        }
                    }
                }
            }
            category("Appearance") {
                radio {
                    key = "test7"
                    default = "Y"
                    backup = true
                    title { literal("Test radio setting") }
                    summary { literal("Test summary") }
                    entries {
                        entries(listOf("A", "B", "C"))
                        values(listOf("Z", "Y", "X"))
                    }
                }
                menu("Empty Menu") {
                    setting {
                        menu {
                            title { literal("This is an empty menu without subtitle") }
                        }
                    }
                }
            }
        }
    }

    PrivacyFriendlyCoreTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            preferences.settings!!.Display {}
        }
    }
}