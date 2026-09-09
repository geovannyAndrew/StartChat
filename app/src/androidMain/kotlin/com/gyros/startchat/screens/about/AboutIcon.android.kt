package com.gyros.startchat.screens.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.gyros.startchat.R

actual @Composable
fun aboutIconPainter(): Painter =
    painterResource(id = R.drawable.ic_launcher_about_icon)
