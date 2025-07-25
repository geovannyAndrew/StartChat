package com.gyros.startchat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gyros.startchat.screens.about.AboutScreen
import com.gyros.startchat.screens.startchat.StartChatScreenWithViewModel

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    onNavigationIconClick: () -> Unit = {}
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = "start_chat",
        builder = {
            composable("start_chat") {
                StartChatScreenWithViewModel(
                    onNavigationIconClick = onNavigationIconClick
                )
            }
            composable("about") {
                AboutScreen(
                    onNavigationIconClick = onNavigationIconClick
                )
            }
        }
    )

}