package com.gyros.startchat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gyros.startchat.ui.theme.StartChatTheme
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent?.action
        val type = intent?.type
        val text = intent?.getStringExtra(Intent.EXTRA_TEXT)
        Log.e("Data0", action.orEmpty())
        Log.e("Data1", type.orEmpty())
        Log.e("Data2", text.orEmpty())
        if(action === Intent.ACTION_SEND) {
            var formattedNumber: String? = null
            val cleanText = text?.trim()?.replace("-","")?.replace(")","")?.replace("(","").orEmpty()
            if(cleanText.length >= 10 || cleanText.length <= 11) {
                formattedNumber = "57${cleanText}";
            }
            else if(cleanText.length> 11) {
                formattedNumber = cleanText
            }
            formattedNumber?.let {
                startIntentAction(formattedNumber = formattedNumber)
            } ?: kotlin.run {
                Log.e("Error", "Invalid string number")
            }
            finish()
        }
        else{
            setContent {
                StartChatTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Greeting("Android")
                    }
                }
            }
        }
    }

    private fun startIntentAction(formattedNumber: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, "https://wa.me/$formattedNumber".toUri())
        startActivity(browserIntent)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StartChatTheme {
        Greeting("Android")
    }
}