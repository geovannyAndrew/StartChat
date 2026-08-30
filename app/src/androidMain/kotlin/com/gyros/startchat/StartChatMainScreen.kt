package com.gyros.startchat

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.gyros.startchat.ui.theme.GreenMenu
import com.gyros.startchat.ui.theme.StartChatTheme
import kotlinx.coroutines.launch

@Composable
fun StartChatMainScreen() {
    val navController: NavHostController = rememberNavController()
    StartChatTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = GreenMenu,
                        drawerContentColor = Color.White
                    ) {
                        val colors = NavigationDrawerItemDefaults.colors(
                            unselectedIconColor = Color.White,
                            unselectedTextColor = Color.White
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            stringResource(R.string.drawer_start_chat_menu),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        HorizontalDivider()
                        NavigationDrawerItem(
                            label = { Text(text = stringResource(R.string.drawer_option_start_chat)) },
                            selected = false,
                            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                            colors = colors,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                }
                                navController.navigate("start_chat")
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text(text = stringResource(R.string.drawer_option_history)) },
                            colors = colors,
                            selected = false,
                            icon = { Icon(Icons.Filled.History, contentDescription = null) },
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                }
                                navController.navigate("history")
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text(text = stringResource(R.string.drawer_option_about)) },
                            colors = colors,
                            selected = false,
                            icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                }
                                navController.navigate("about")
                            }
                        )
                    }
                },
            ) {
                MainNavHost(
                    navController = navController,
                    onNavigationIconClick = {
                        scope.launch {
                            if (drawerState.isOpen) {
                                drawerState.close()
                            } else {
                                drawerState.open()
                            }
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartChatPreview() {
    StartChatTheme {
        StartChatMainScreen()
    }
}