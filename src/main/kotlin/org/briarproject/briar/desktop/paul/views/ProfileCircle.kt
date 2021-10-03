package org.briarproject.briar.desktop.paul.views

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun PreviewProfileCircle() {
    val bytes = byteArrayOf(
        -110, 58, 34, -54, 79, 0, -92, -65, 2, 10, -7, 53, -121,
        -31, 39, 48, 86, -54, -4, 7, 108, -106, 89, 11, 65, -118,
        13, -51, -96, 38, -91
    )
    ProfileCircle(90.dp, bytes)
}

@Composable
fun ProfileCircle(size: Dp, input: ByteArray) {
    Canvas(Modifier.size(size).clip(CircleShape).border(2.dp, Color.White, CircleShape)) {
        Identicon(input, this.size.width, this.size.height).draw(this)
    }
}
