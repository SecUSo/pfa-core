package org.secuso.privacyfriendlycore.ui.stage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

val multiStageItemModifier = { onClick: () -> Unit ->
    Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(top = 16.dp, bottom = 16.dp, start = 32.dp, end = 32.dp) }

@Composable
fun SimpleMultiStageItem(name: String, onClick: () -> Unit) {
    Row(multiStageItemModifier(onClick)) {
        Text(text = name)
    }
}

@Composable
fun IconMultiStageItem(icon: ImageVector, name: String, onClick: () -> Unit) {
    Row(multiStageItemModifier(onClick)) {
        Icon(imageVector = icon, contentDescription = null)
        Text(text = name)
    }
}

@Composable
fun <S : Stage> MultiStageMenu(stages: List<MultiStage.SingleStage<S>>) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "menu") {
        composable("menu") {
            LazyColumn(Modifier.fillMaxWidth()) {
                items(count = stages.size) {
                    stages[it].display {
                        navController.navigate("_${stages[it].name}")
                    }
                }
            }
        }
        for (stage in stages) {
            composable("_${stage.name}") { stage.stage.composable() }
        }
    }
}