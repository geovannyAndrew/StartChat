package com.gyros.startchat

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gyros.startchat.screens.startchat.StartChatScreenWithViewModel

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = "start_chat",
        builder = {
            composable("start_chat") {
                StartChatScreenWithViewModel()
            }
            composable("about") {
                Text("About")
            }
        }
    )

}