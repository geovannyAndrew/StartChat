package com.gyros.startchat.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gyros.startchat.data.models.ChatHistoryEntry
import com.gyros.startchat.ui.theme.Green
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** Scaffold for the Chat History screen with top bar and entry list. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatHistoryScreen(
    modifier: Modifier = Modifier,
    state: ChatHistoryViewModel.ChatHistoryState,
    onNavigationIconClick: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Green,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(
                        "History",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClick) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Open main menu",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        ChatHistoryContent(
            modifier = Modifier.padding(innerPadding),
            state = state
        )
    }
}

/** Displays the list of chat history entries, or an empty-state message if none exist. */
@Composable
fun ChatHistoryContent(
    modifier: Modifier = Modifier,
    state: ChatHistoryViewModel.ChatHistoryState
) {
    if (state.entries.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No chat history yet.")
        }
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(state.entries) { entry ->
                ChatHistoryItem(
                    entry = entry,
                    onClick = { state.onEntryClicked?.invoke(entry) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun ChatHistoryItem(
    entry: ChatHistoryEntry,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = entry.phoneNumber)
        Text(
            text = formatTimestamp(entry.timestamp),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

internal fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val month = dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
    return "$month ${dateTime.dayOfMonth}, ${dateTime.year}\n${
        dateTime.hour.toString().padStart(2, '0')
    }:${dateTime.minute.toString().padStart(2, '0')}"
}
