package com.gyros.startchat.screens.startchat

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.gyros.startchat.R
import com.gyros.startchat.common.composables.DropdownCountries
import com.gyros.startchat.ui.theme.Green

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartChatScreen(
    modifier: Modifier = Modifier,
    state: StartChatViewModel.StartChatState,
    isDialog: Boolean = false,
    onNavigationIconClick: () -> Unit = {}
) {
    if (isDialog) {
        StartChatContent(
            state = state,
            isDialog = true
        )
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Green,
                        titleContentColor = Color.White
                    ),
                    title = {
                        Text("Start Chat", maxLines = 1, overflow = TextOverflow.Ellipsis)
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
            },
        ) { innerPadding ->
            StartChatContent(
                modifier = Modifier.padding(innerPadding),
                state = state
            )
        }
    }
}

@Preview
@Composable
fun StartChatScreenPreview() {
    StartChatScreen(
        state = StartChatViewModel.StartChatState(
            countryCodes = listOf(),
        )
    )
}

@Composable
fun StartChatScreenWithViewModel(
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onNavigationIconClick: () -> Unit = {}
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val activity = LocalActivity.current
    val viewModel = hiltViewModel<StartChatViewModel>()
    val context = LocalContext.current
    val view = LocalView.current
    LaunchedEffect(viewModel, lifecycle) {
        viewModel.start(
            actionText = actionText
        )
        viewModel.events.collect { event->
            when (event) {
                is StartChatViewModel.Events.StartIntentAction -> {
                    val browserIntent = Intent(Intent.ACTION_VIEW, event.uri)
                    context.startActivity(browserIntent)
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

@Composable
private fun StartChatContent(
    modifier: Modifier = Modifier,
    state: StartChatViewModel.StartChatState,
    isDialog: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxSize().apply {
                if (!isDialog) {
                    background(Color.LightGray)
                }
            }.imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.width(332.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                if (!state.numbersOnClipBoard.isNullOrEmpty()) {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp)),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        item {
                            Text(
                                stringResource(R.string.start_chat_numbers_from_clipboard),
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    platformStyle = PlatformTextStyle(),
                                    fontFamily = FontFamily.Default,
                                    fontWeight = FontWeight.W800
                                ),
                            )
                        }
                        items(state.numbersOnClipBoard) { item ->
                            OutlinedButton(
                                onClick = {
                                    state.onEditTextChange(item)
                                }
                            ) {
                                Text(
                                    item,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        platformStyle = PlatformTextStyle(),
                                        fontFamily = FontFamily.Default,
                                    ),
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                val alpha = if (state.countryCodes != null) {
                    1F
                } else {
                    0F
                }
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    DropdownCountries(
                        modifier = Modifier.alpha(alpha),
                        countryCodeSelected = state.selectedCountryCode,
                        countryCodes = state.countryCodes,
                        onCountryCodeSelected = state.onCountryCodeSelected
                    )
                    if(state.countryCodes == null) {
                        Text(
                            stringResource(R.string.start_chat_number_with_country_code),
                            style = TextStyle(
                                fontSize = 18.sp,
                                platformStyle = PlatformTextStyle(),
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.W800
                            ),
                        )
                    }
                }
                Spacer(
                    Modifier.height(8.dp)
                )
                OutlinedTextField(
                    singleLine = true,
                    value = state.phoneNumber,
                    onValueChange = { newText ->
                       state.onEditTextChange(newText)
                    },
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        platformStyle = PlatformTextStyle(),
                        fontFamily = FontFamily.Default
                    ),
                    placeholder = { Text(stringResource(R.string.start_chat_enter_phone_number)) }, // 4. Placeholder as Composable
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(
                    Modifier.height(16.dp)
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green
                    ),
                    enabled = state.onStartChat != null,
                    onClick = {
                        state.onStartChat?.invoke(
                            state.selectedCountryCode,
                            state.phoneNumber
                        )
                    }
                ) {
                    Text(
                        text = stringResource(R.string.start_chat_button_cta),
                        style = TextStyle(
                            fontSize = 18.sp,
                            platformStyle = PlatformTextStyle(),
                            fontFamily = FontFamily.Default
                        ),
                    )
                }
            }
        }
        Spacer(
            Modifier.height(24.dp)
        )
    }
}
