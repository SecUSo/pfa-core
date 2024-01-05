package org.secuso.privacyfriendlycore.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun CenterLines(modifier: Modifier = Modifier, sentences: Iterable<String>) {
    Text(text = sentences.joinToString(System.lineSeparator()), modifier = modifier.fillMaxWidth(), textAlign = TextAlign.Center)
}

@Composable
fun CenterText(modifier: Modifier = Modifier, text: String) {
    Text(text = text, modifier = modifier.fillMaxWidth(), textAlign = TextAlign.Center)
}