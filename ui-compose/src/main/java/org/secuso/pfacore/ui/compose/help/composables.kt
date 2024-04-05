package org.secuso.pfacore.ui.compose.help

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HelpTitle(modifier: Modifier, text: String) {
    Text(text = text, modifier = modifier, fontWeight = FontWeight.Bold)
}

@Composable
fun HelpDescription(modifier: Modifier, text: String) {
    Text(text = text, modifier = modifier, style = MaterialTheme.typography.bodyMedium)
}

@Composable
fun HelpMenuItem(
    title: @Composable (Modifier) -> Unit,
    description: @Composable (Modifier) -> Unit,
    modifier: Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    Column(
        Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = "",
                modifier = Modifier
                    .padding(8.dp)
                    .rotate(if (expanded) 180f else 0f)
            )
            title(
                modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, top = 8.dp, bottom = 8.dp)
            )
        }
        if (expanded) {
            description(
                modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp)
            )
        }
    }
}

@Composable
fun HelpMenu(items: List<HelpData>) {
    LazyColumn(Modifier.fillMaxWidth()) {
        items(count = items.size) {
            items[it].Display()
        }
    }
}