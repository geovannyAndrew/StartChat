package com.gyros.startchat.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

actual val MenuIcon: ImageVector
    get() {
        val stroke = SolidColor(Color.Black)
        val strokeWidth = 2f
        val cap = StrokeCap.Round
        val join = StrokeJoin.Round

        return ImageVector.Builder(
            name = "Menu",
            defaultWidth = 24f.dp,
            defaultHeight = 24f.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).build()
    }