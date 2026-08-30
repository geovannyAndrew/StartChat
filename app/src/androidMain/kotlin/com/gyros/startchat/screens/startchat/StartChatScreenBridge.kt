package com.gyros.startchat.screens.startchat

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.gyros.startchat.data.UrlOpenerImpl
import org.koin.androidx.compose.koinViewModel

@Composable
fun StartChatScreenWithViewModel(
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onNavigationIconClick: () -> Unit = {}
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val activity = LocalActivity.current
    val viewModel = koinViewModel<StartChatViewModel>()
    val context = LocalContext.current
    val view = LocalView.current
    val urlOpener = remember { UrlOpenerImpl(context) }
    LaunchedEffect(viewModel, lifecycle) {
        viewModel.start(
            actionText = actionText
        )
        viewModel.events.collect { event ->
            when (event) {
                is StartChatViewModel.Events.StartIntentAction -> {
                    urlOpener.open(event.uri)
                    actionText?.let {
                        activity?.finish()
                    }
                }
            }
        }
    }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                view.post {
                    viewModel.onResume()
                }
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
