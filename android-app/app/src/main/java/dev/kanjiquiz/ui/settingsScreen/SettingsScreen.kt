package dev.kanjiquiz.ui.settingsScreen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, modifier: Modifier) {
    val state by viewModel.uiState.collectAsState()

    if (state.loading) {
        Text("Loading", fontSize = 50.sp, modifier = Modifier.fillMaxWidth())
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(viewModel.uiState.value.allTags) { tag ->
            val checked = tag in state.selectedTags

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { viewModel.onCheckedChange(tag, it) }
                )
                Text(text = tag, fontSize = 20.sp)
            }
        }
    }
}

