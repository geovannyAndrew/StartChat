package com.gyros.startchat.screens.startchat

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.gyros.startchat.ui.theme.Green

@Composable
fun StartChatScreen(
    modifier: Modifier = Modifier,
    state: StartChatViewModel.StartChatState,
    isDialog: Boolean = false
) {
    if (isDialog) {
        StartChatContent(
            state = state,
            isDialog = true
        )
    } else {
        Scaffold { innerPadding ->
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
        state = StartChatViewModel.StartChatState()
    )
}

@Composable
fun StartChatScreenWithViewModel(
    modifier: Modifier = Modifier,
    actionText: String? = null
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val activity = LocalActivity.current
    val viewModel = hiltViewModel<StartChatViewModel>()
    actionText?.let {
        viewModel.processText(it)
    } ?: run {
        viewModel.loadCountryCodes()
    }
    val context = LocalContext.current
    LaunchedEffect(viewModel, lifecycle) {
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

    val state by viewModel.state.collectAsState()
    StartChatScreen(
        modifier = modifier,
        state = state,
        isDialog = actionText != null
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
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var expanded by remember { mutableStateOf(false) }
        var selectedCountryCode by remember { mutableStateOf(state.selectedCountryCode) }
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
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clickable {
                                expanded = true
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedCountryCode?.let {
                                "${it.dialCode} ${it.flag}"
                            } ?: "",
                            style = TextStyle(
                                fontSize = 18.sp,
                                platformStyle = PlatformTextStyle()
                            )
                        )
                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )
                        Text(
                            text = selectedCountryCode?.name ?: "Select Country Code",
                            color = Color.Gray,
                            style = TextStyle(
                                fontSize = 18.sp,
                                platformStyle = PlatformTextStyle(),
                                fontFamily = FontFamily.Default
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "▼",
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        state.countryCodes.forEach { countryCode ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "${countryCode.dialCode} ${countryCode.flag} ${countryCode.name}",
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            platformStyle = PlatformTextStyle(),
                                            fontFamily = FontFamily.Default
                                        ),
                                    )
                                },
                                onClick = {
                                    selectedCountryCode = countryCode
                                    expanded = false
                                    state.onCountryCodeSelected(countryCode)
                                }
                            )
                        }
                    }
                }
                Spacer(
                    Modifier.height(8.dp)
                )
                var textState by remember { mutableStateOf(state.phoneNumber) }
                OutlinedTextField(
                    singleLine = true,
                    value = textState,
                    onValueChange = { newText ->
                        textState = newText                    // 3. Update the state
                    },
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        platformStyle = PlatformTextStyle(),
                        fontFamily = FontFamily.Default
                    ),
                    placeholder = { Text("Enter phone number") }, // 4. Placeholder as Composable
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Gray,
                        unfocusedBorderColor = Color.Gray
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(
                    Modifier.height(8.dp)
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green
                    ),
                    onClick = {
                        state.onStartChat(
                            selectedCountryCode,
                            textState
                        )
                    }
                ) {
                    Text(
                        text = "Start Chat",
                        style = TextStyle(
                            fontSize = 18.sp,
                            platformStyle = PlatformTextStyle(),
                            fontFamily = FontFamily.Default
                        ),
                    )
                }
            }
        }
    }
}
