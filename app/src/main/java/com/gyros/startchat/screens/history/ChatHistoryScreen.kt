package com.gyros.startchat.screens.history

import android.content.Intent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gyros.startchat.R
import com.gyros.startchat.data.models.ChatHistoryEntry
import com.gyros.startchat.ui.theme.Green
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                        stringResource(R.string.toolbar_history_title),
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

@Composable
fun ChatHistoryScreenWithViewModel(
    modifier: Modifier = Modifier,
    onNavigationIconClick: () -> Unit = {}
) {
    val viewModel = hiltViewModel<ChatHistoryViewModel>()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.load()
        viewModel.events.collect { event ->
            when (event) {
                is ChatHistoryViewModel.Events.OpenWhatsApp ->
                    context.startActivity(Intent(Intent.ACTION_VIEW, event.uri))
            }
        }
    }

    val state by viewModel.state.collectAsState()
    ChatHistoryScreen(
        modifier = modifier,
        state = state,
        onNavigationIconClick = onNavigationIconClick
    )
}

@Composable
private fun ChatHistoryContent(
    modifier: Modifier = Modifier,
    state: ChatHistoryViewModel.ChatHistoryState
) {
    if (state.entries.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.history_empty_state))
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

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d, yyyy\nHH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Preview
@Composable
fun ChatHistoryScreenPreview() {
    ChatHistoryScreen(
        state = ChatHistoryViewModel.ChatHistoryState(
            entries = listOf(
                ChatHistoryEntry("+14155552671", 1714600000000L),
                ChatHistoryEntry("+573212345678", 1714500000000L)
            )
        )
    )
}
