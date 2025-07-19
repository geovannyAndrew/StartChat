package com.gyros.startchat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gyros.startchat.ui.theme.StartChatTheme
import com.gyros.startchat.data.models.CountryCode
import com.gyros.startchat.ui.theme.Green
import com.gyros.startchat.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val action = intent?.action
        val type = intent?.type
        val text = intent?.getStringExtra(Intent.EXTRA_TEXT)
        Log.e("Data0", action.orEmpty())
        Log.e("Data1", type.orEmpty())
        Log.e("Data2", text.orEmpty())
        if(action === Intent.ACTION_SEND) {
            text?.let {
                viewModel.processText(text)
            }
        }
        else {
            viewModel.loadCountryCodes()
        }
        setContent {
            val state by viewModel.state.collectAsState()
            state.actionUri?.let { uri ->
                startIntentAction(
                    uri = uri
                )
                finish()
            } ?: run {
                StartChatMainScreen(
                    state = state,
                    onCountryCodeSelected = { countryCode ->
                        viewModel.selectCountryCode(countryCode)
                    },
                    onStartChat = { countryCode, phoneNumber ->
                        countryCode?.let {
                            viewModel.startChat(
                                phoneNumber = phoneNumber,
                                countryCode = it
                            )
                        }
                    }
                )
            }
        }
    }

    private fun startIntentAction(uri: Uri) {
        val browserIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(browserIntent)
    }
}

@Composable
fun StartChatMainScreen(
    state: MainViewModel.StartChatState,
    onCountryCodeSelected: (CountryCode) -> Unit,
    onStartChat: (CountryCode?, String) -> Unit
) {
    StartChatTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ModalNavigationDrawer(
                drawerContent = {
                    ModalDrawerSheet {
                        Text("Drawer title", modifier = Modifier.padding(16.dp))
                        HorizontalDivider()
                        NavigationDrawerItem(
                            label = { Text(text = "Drawer Item") },
                            selected = false,
                            onClick = { /*TODO*/ }
                        )
                    }
                },
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    var selectedCountryCode by remember { mutableStateOf(state.selectedCountryCode) }
                    Box(
                        modifier = Modifier.width(300.dp)
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
                                        onCountryCodeSelected(countryCode)
                                    }
                                )
                            }
                        }
                    }
                    var textState by remember { mutableStateOf(state.phoneNumber) }
                    OutlinedTextField(
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
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .width(300.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Gray,
                            unfocusedBorderColor = Color.Gray
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    Button(
                        modifier = Modifier.width(300.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Green
                        ),
                        onClick = {
                            onStartChat(
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
                /*
                LazyColumn {
                    items(state.countryCodes) { countryCode ->
                        Text(
                            text = countryCode.flag,
                            style = TextStyle(
                                fontSize = 30.sp,
                                platformStyle = PlatformTextStyle(
                                    emojiSupportMatch = EmojiSupportMatch.None
                                )/* ... */
                            )
                        )
                    }
                }*/
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogStartChat() {
    BasicAlertDialog (
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
        },
        modifier = Modifier.padding(16.dp),
    ) {

    }
}

@Preview(showBackground = true)
@Composable
fun StartChatPreview() {
    StartChatTheme {
        StartChatMainScreen(
            state = MainViewModel.StartChatState(),
            onCountryCodeSelected = {},
            onStartChat = { _, _ -> }
        )
    }
}