package com.gyros.startchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gyros.startchat.screens.startchat.StartChatScreenWithViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val action = intent?.action
        val type = intent?.type
        val text = intent?.getStringExtra(Intent.EXTRA_TEXT)
        Log.e("Data0", action.orEmpty())
        Log.e("Data1", type.orEmpty())
        Log.e("Data2", text.orEmpty())
        if (action === Intent.ACTION_SEND) {
            setTheme(R.style.Theme_StartChat_Transparent)
        }
        super.onCreate(savedInstanceState)
        if (action === Intent.ACTION_SEND) {
            text?.let {
                setContent {
                    StartChatScreenWithViewModel(
                        actionText = it,
                    )
                }
            }
        } else {
            setContent {
                StartChatMainScreen()
            }
        }

    }


}