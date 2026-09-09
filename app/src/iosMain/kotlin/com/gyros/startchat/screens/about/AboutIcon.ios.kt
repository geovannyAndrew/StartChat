package com.gyros.startchat.screens.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

private val path1Nodes = PathParser().parsePathString(
    "M122.1,40.53h-44.49c-15.99,0-28.94,12.96-28.94,28.94v32.5c0,11.48,6.69,21.41,16.38,26.08l-0.15,12.75c-0.06,5.51,6.24,8.69,10.63,5.36l19.27-14.61c0.54-0.41,1.2-0.63,1.88-0.63h25.71c15.99,0,28.94-12.96,28.94-28.94v-32.21c0-16.15-13.09-29.24-29.24-29.24ZM141.17,102.39c0,10.15-8.23,18.38-18.38,18.38h-29.17c-1.07,0-2.11,0.36-2.96,1.02l-8.92,6.95-7.08,5.52v-13.64s0,0,0,0h0s0,0,0,0c-9.19-1.19-16.29-9.04-16.29-18.56v-32.98c0-10.15,8.23-18.38,18.38-18.38h46.05c10.15,0,18.38,8.23,18.38,18.38v33.32Z"
).toNodes()

private val path2Nodes = PathParser().parsePathString(
    "M124.59,88.69l-20.89,20.61c-2.02,2-5.28,1.98-7.28-0.05h0c-2-2.02-1.98-5.28,0.05-7.28l10.55-10.41h-32.25c-2.84,0-5.15-2.31-5.15-5.15h0c0-2.84,2.31-5.15,5.15-5.15h32.25l-10.56-10.41c-2.02-2-2.05-5.26-0.05-7.28h0c2-2.03,5.26-2.05,7.28-0.05l20.89,20.59c1.28,1.26,1.28,3.32,0,4.58Z"
).toNodes()

@Composable
actual fun aboutIconPainter(): Painter {
    val imageVector = ImageVector.Builder(
        name = "ic_launcher_about_icon",
        defaultWidth = 200.dp,
        defaultHeight = 200.dp,
        viewportWidth = 200f,
        viewportHeight = 200f
    ).apply {
        addPath(pathData = path1Nodes, fill = SolidColor(Color.White))
        addPath(pathData = path2Nodes, fill = SolidColor(Color.White))
    }.build()

    return rememberVectorPainter(imageVector)
}
