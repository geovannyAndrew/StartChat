package com.gyros.startchat.screens.startchat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.gyros.startchat.data.UrlOpener
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StartChatScreenWithViewModel(
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onStartChatLaunched: () -> Unit = {},
    onNavigationIconClick: () -> Unit = {}
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val viewModel = koinViewModel<StartChatViewModel>()
    val urlOpener = koinInject<UrlOpener>()

    LaunchedEffect(viewModel, lifecycle) {
        viewModel.start(actionText = actionText)
        viewModel.events.collect { event ->
            when (event) {
                is StartChatViewModel.Events.StartIntentAction -> {
                    urlOpener.open(event.uri)
                    if (actionText != null) {
                        onStartChatLaunched()
                    }
                }
            }
        }
    }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onResume()
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    val state by viewModel.state.collectAsState()
    StartChatScreen(
        modifier = modifier,
        state = state,
        isDialog = actionText != null,
        onNavigationIconClick = onNavigationIconClick
    )
}
